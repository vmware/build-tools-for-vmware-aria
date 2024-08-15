/*-
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

/**
 * The node holds information about its position
 */
export interface GraphNode {
	name: string;
	origName: string;
	x?: number;
	y?: number;
	targets: string[];
	sources?: string[];
}

/**
 * Tree representation with grid coordinates for its elements.
 * Holds a {@link GraphNode} with its grid row and column, and an array of Tree branches for its children.
 */
interface GridTree {
	node: GraphNode;
	branches: GridTree[];
	rootRow?: number;
	rootCol?: number;
}

/**
 * Tracks the position within {@link GridTree} currently being populated.
 * After building the tree, holds the dimensions of the grid.
 */
interface GridTracker {
	lastRow: number; // last reached row; only increments
	lastCol: number; // last reached col
	currentCol?: number; // tracks tree level during recursion
}

/**
 * Wrapper for an array of {@link GraphNode}s (with names and targets),
 * which on initialization populates the nodes' canvas positions (x,y)
 * to reflect a tree graph structure with grid coordinates, in which:
 * - the root of the tree will be the default Start node
 * - the root node targets will be its children
 * - branches represent node targets in order, unless already visited (when targeted by multiple sources)
 * - leaves (branches without children) represent workflow End elements (or all their targets were already visited)
 * - the tree structure is defined by each node's primary source (first node from the array to target it)
 * Grid coordinates represent row and column within a grid, where:
 * - row 0 is the coordinate for the first encountered leaf when building from the root (depth-first)
 * - rows increase by 2 for each encountered leaf (leaving a blank row)
 * - root is at column 0, same row as its first target node
 * - the first node from the root targets will be placed at col 1
 * - the remaining root node targets will be displayed at column 0 as secondary root elements.
 *   This adjustment will be reflected in the columns of their branches
 * Node coordinates will [x,y] be calculated based on grid coordinates and node spacing:
 * - x: [initial offset] + [node column ] * [horizontal spacing];
 * - y: [initial offset] + [node row ] * [vertical spacing];
 * where:
 * - [horizontal spacing] is the horizontal distance between adjacent nodes (as points, in canvas units)
 *   See param nodeSpacing.
 * - [vertical spacing] = [initial offset] = 0.5 * [horizonatal spacing]
 * Example:
```
const nodes = [
	{name: "start", origName: "Start", targets: ["item6","item9"]}, // default start; targets the root and default error handler items
	{name: "item1" origName: "decisionElement", targets: ["waitForEvent", "prepareItems"]},
	{name: "item2", origName: "prepareItems", targets: ["callOtherWf"]},
	{name: "item3", origName: "callOtherWf", targets: ["print"]},
	{name: "item4", origName: "print", targets: ["end"]},
	{name: "item5", origName: "execute", targets: ["decisionElement"]},
	{name: "item6", origName: "prepare", targets: ["execute"]}, // root item
	{name: "item7", origName: "waitForEvent", targets: ["execute"]},
	{name: "item8", origName: "workflowEnd", targets: []}, // secondary end
	{name: "item9", origName: "defaultErrorHandler", targets: ["workflowEnd"]}, // default error handler (secondary start)
	{name: "item0", origName: "End0", targets: [""]}, // default end
]

new Graph(nodes).draw();
```
*
* Will output diagram of the type (compact, secondary links not displayed):
*
```
Workflow diagram
--------------------------------------------------------------------------------------------------------------------------------------------------------------------
																							/[item7:waitForEvent]
>[       start:Start       ]--[  item6:prepare  ]--[item5:execute]--[item1:decisionElement]-
																							\[item2:prepareItems]--[item3:callOtherWf]--[item4:print]--[item0:End0]

>[item9:defaultErrorHandler]--[item8:workflowEnd]
--------------------------------------------------------------------------------------------------------------------------------------------------------------------
Secondary connections (not displayed):
item7 -> item5
```
 */
export class Graph {
	/** Name of the Default Start node */
	public static readonly START = "start";

	/**
	 * Will hold the calculated tree.
	 * Tree root is the Start node, its children will be calculated recursively
	 * based on (visited) node targets (see {@link buildGridTree}).
	 */
	private readonly tree: GridTree;

	/**
	 * Will hold grid dimensions after the tree is calculated;
	 * used during tree calculation to track current row/column index.
	 */
	private readonly gridTracker: GridTracker = { currentCol: 0, lastRow: 0, lastCol: 0 };

	/**
	 * Record of all visited nodes during tree calculation. Object keys are the node names.
	 * Will hold all connected nodes at the end of tree calculation.
	 */
	private readonly visitedNodes: Record<string, GridTree> = {};

	/**
	 * @param {GraphNode[]} nodes - List of the nodes that will be used to build the tree.
	 * Must contain a Start node with name={@link START 'start'} with at least one target node.
	 * The first target will be displayed as its child.
	 * Any remaining targets will be displayed as secondary starting elements (at the same level as the default Start)
	 * @param {number} [nodeSpacing = 120] - horizontal spacing in canvas units between adjacent nodes (vertical spacing is half)
	 * @param {[number, number]} [maxGridDimensions = [10, 50]] - maximum number of grid rows and columns
	 */
	constructor(
		private readonly nodes: GraphNode[],
		public readonly nodeSpacing: number = 120,
		maxGridDimensions: [number, number] = [10, 50]
	) {
		this.tree = { node: this.getNode(Graph.START), branches: [] };

		this.populateNodeSources();
		this.buildGridTree(this.tree);
		this.gridTracker.lastRow--; // compensate for extra row

		// set node x/y based on rows and colums:
		const gridDimensions = this.getDimensions();
		if (gridDimensions[0] > maxGridDimensions[0] || gridDimensions[1] > maxGridDimensions[1]) {
			throw new Error(`Grid dimensions ${gridDimensions} exceed the allowed number of rows/colums ${maxGridDimensions}!`);
		}
		this.treeWalk(this.tree, t => {
			t.node.x = Math.round(nodeSpacing * (0.5 + t.rootCol));
			t.node.y = Math.round(nodeSpacing / 2 * (1 + t.rootRow));
		});
	}

	// Validates starting nodes and populates sources. First encountered defines tree structure, others are secondary.
	populateNodeSources(): void {
		this.nodes?.forEach(node => {
			node.sources = this.nodes.filter(n => n.targets?.indexOf(node.name) > -1).map(n => n.name);
		});
		const unlinkedNodes = this.nodes.filter(n => !n.sources?.length && n.name !== Graph.START);
		if (unlinkedNodes.length) {
			throw new Error(`There are disconnected nodes: ${unlinkedNodes.map(n => `${n.name}(${n.origName})`).join()}!`);
		}
	}

	buildGridTree(tree: GridTree): void {
		tree.node.targets.map(t => this.getNode(t)) // throws
			.filter(node => node.name === Graph.START || tree.node.name === node.sources[0])
			.forEach(node => {
				if (!(node.name in this.visitedNodes)) {
					// secondary start nodes will be placed in col. 0:
					const colIncrement = this.tree.node.targets.indexOf(node.name) > 0 ? 0 : 1;
					try {
						this.gridTracker.currentCol += colIncrement;
						this.gridTracker.lastCol = Math.max(this.gridTracker.currentCol + 1, this.gridTracker.lastCol);
						const branch: GridTree = { node, branches: [] };
						this.visitedNodes[node.name] = branch;
						tree.branches.push(branch);
						this.buildGridTree(branch); // depth-first
					} finally {
						this.gridTracker.currentCol -= colIncrement;
					}
				}
			});
		// Rows and columns will be populated from leaves to root (depth first).
		this.calculateGridPosition(tree);
	}

	private calculateGridPosition(tree: GridTree) {
		tree.rootCol = Math.max(tree.rootCol || 0, this.gridTracker.currentCol);
		if (!tree.branches?.length) { // leaf (no children); put on lastRow and increment it for the next leaf
			tree.rootRow = this.gridTracker.lastRow++;
			this.gridTracker.lastRow++; // extra row
			tree.branches = null;
		}
		else if (tree.node.name === Graph.START) { // start node is on same row as first branch
			tree.rootRow = tree.branches[0].rootRow;
		}
		else { // branch has children - put on row approximately between the first and last child's;
			tree.rootRow = Math.floor((tree.branches[0].rootRow + tree.branches[tree.branches.length - 1].rootRow) / 2);
			// in case of back reference on the same row - avoid arrows crossing intermediate node(s)
			while (tree.node.sources.find((root, ind) => {
				const visitedNode = !ind ? null : this.visitedNodes[root];
				return visitedNode?.rootRow === tree.rootRow && visitedNode.rootCol > tree.rootCol + 1;
			})) {
				tree.rootRow++;
				this.gridTracker.lastRow = Math.max(this.gridTracker.lastRow, tree.rootRow);
			}
		}
	}

	/**
	 * @returns {[number, number]} - number of rows and columns in the grid
	 */
	getDimensions(): [number, number] {
		return [this.gridTracker.lastRow, this.gridTracker.lastCol];
	}

	public getNode(name: string): GraphNode {
		const node = this.nodes.find(node => node.name === name);
		if (!node) {
			throw new Error(`Node "${name}" not found`);
		}

		return node;
	}

	/**
	 * Use to draw a graph in the console
	 *
	 * @WARN: Used for debugging purposes
	 */
	public draw(label: string = "Workflow diagram"): this {
		const blankRow: string[] = Array.from({ length: this.gridTracker.lastCol }, () => "");
		this.treeWalk(this.tree, (b) => {
			blankRow[b.rootCol] = blankRow[b.rootCol].padEnd(
				Math.max(blankRow[b.rootCol].length, 5 + b.node.name.length + b.node.origName.length)
			)
		});
		const grid = Array.from({ length: this.gridTracker.lastRow }, () => [...blankRow]);
		const getSiblings = (branch: GridTree) => this.visitedNodes[branch.node?.sources[0]]?.branches || [];
		const getPrefix = (branch: GridTree) => branch.rootCol === 0 ? ">[" // root
			: (branch.rootCol === 1 || getSiblings(branch).length === 1 ? "-[" // only branch (incl. from start)
				: (getSiblings(branch)[0] === branch ? "/[" // first branch
					: (getSiblings(branch)[getSiblings(branch).length - 1] === branch ? "\\[" // last branch
						: "|["))) // middle branch
		const getSuffix = (branch: GridTree) => !branch.branches?.length ? "] " : "]-";
		this.treeWalk(this.tree, (branch: GridTree) => {
			let nodeStr = `${branch.node.name}:${branch.node.origName}`;
			const maxNodeNameLength = blankRow[branch.rootCol].length - 4;
			nodeStr = nodeStr.padStart(nodeStr.length + Math.floor((maxNodeNameLength - nodeStr.length) / 2));
			grid[branch.rootRow][branch.rootCol] = `${getPrefix(branch)}${nodeStr.padEnd(maxNodeNameLength)}${getSuffix(branch)}`;
		});

		const rows = grid.map(line => line.join(''));
		const border = Array(rows[0]?.length || 0).fill("-").join("");

		const omittedConnections = [];
		this.treeWalk(this.tree, (branch) => {
			omittedConnections.push(...(branch.node.sources || []).filter((src, ind) => !!ind).map(src => `${src} -> ${branch.node.name}`));
		});
		if (omittedConnections.length) {
			omittedConnections.unshift("Secondary connections (not displayed):");
		}

		console.debug([label, border, ...rows, border, ...omittedConnections].join('\n'));
		return this;
	}

	private treeWalk(tree: GridTree, fnPre: (t: GridTree) => void, fnPost: (t: GridTree) => void = (t) => { }) {
		fnPre(tree);
		tree.branches?.forEach(branch => this.treeWalk(branch, fnPre, fnPost));
		fnPost(tree);
	}
}
