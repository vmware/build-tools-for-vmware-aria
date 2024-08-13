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
 * Tree representation of a (part of) workflow diagram with grid coordinates for its elements.
 * Holds a {@link GraphNode} with its grid row and column, and an array of Tree branches for its children.
 * When complete:
 * - the root of the tree will be an empty node (not to be displayed)
 * - its children will be the start nodes (Start, [Default Error Handler])
 * - branches represent node targets in order, unless already visited (when targeted by multiple sources)
 * - leaves (branches without children) represent workflow End elements
 * Coordinates represent row and column within a grid, where:
 * - row 0 is the coordinate for the first encountered leaf when building from the root (depth-first)
 * - rows increase by 2 for each encountered leaf (leaving a blank row)
 * Example:
```
const nodes = [
	// First Start
	{ name: "A", targets: ["B"] },
	{ name: "B", targets: ["C"] },
	{ name: "C", targets: ["G", "D"] },
	{ name: "D", targets: ["E"] },
	{ name: "E", targets: ["C", "F"] },
	{ name: "F", targets: ["O"] },
	{ name: "G", targets: ["H"] },
	{ name: "H", targets: ["I"] },
	{ name: "I", targets: ["J", "K", "L", "M"] },
	{ name: "J", targets: [] },
	{ name: "K", targets: [] },
	{ name: "L", targets: [] },
	{ name: "M", targets: [] },
	{ name: "O", targets: ["P"] },
	{ name: "P", targets: ["Q"] },
	{ name: "Q", targets: [] },

	// Second start
	{ name: "S", targets: ["T"] },
	{ name: "T", targets: ["U", "W", "D"] },
	{ name: "U", targets: [] },
	{ name: "W", targets: [] },

	// Third Start is no longer supported
	// { name: "X", targets: ["Y"] },
	// { name: "Y", targets: [] },
];

const graph = new Graph(nodes.map(n => ({...n, origName: n.name})), ["A", "S"]);

console.log('\n' + graph.draw() + '\n');

```
*
* Will output diagram of the type (compact, secondary links not displayed):
*
```
			   /[D]--[E]--[F]--[O]--[P]--[Q]
>[A]--[B]--[C]-				  /[J]
			   \[G]--[H]--[I]-|[K]
							  |[L]
							  \[M]
>[S]--[T]-/[U]
		  \[W]
```
 */
export class Graph {
	/** Name of start node (added automatically, used to calculate start element position) */
	public static START = "start";

	/**
	 * Will hold the calculated tree.
	 * Tree root is an empty node, its children will be the start nodes, their children will be
	 * calculated recursively based on (visited) node targets (see {@link buildGridTree}).
	 */
	private tree: GridTree = { node: null, branches: [] };
	/**
	 * Will hold grid dimensions after the tree is calculated;
	 * used during tree calculation to track current row/column index.
	 */
	private gridTracker: GridTracker = { currentCol: -1, lastRow: 0, lastCol: 0 };
	/**
	 * Record of all visited nodes during tree calculation. Object keys are the node names.
	 * Will hold all connected nodes at the end of tree calculation.
	 */
	private visitedNodes: Record<string, GridTree> = {};

	/**
	 * @param {GraphNode[]} nodes - List of the nodes that will be used to build the tree
	 * @param {string[]} startNodeNames - List of the names of the nodes that will be used as the start of the tree
	 * @param {boolean} [compact = false] - If false, adds an additional row between leaf nodes
	 */
	constructor(
		private nodes: GraphNode[],
		private startNodeNames: string[],
		private compact = false
	) {
		if (!this.nodes?.length || this.nodes.find(n => !n?.name || !Array.isArray(n?.targets))) {
			throw new Error(`Invalid nodes: ${JSON.stringify(this.nodes)}`);
		}
		if (!this.startNodeNames?.length || this.startNodeNames.length > 2) {
			throw new Error(`Number of starting nodes must be 1 (Root element) or 2 (Root element, Default Error Handler element)!`)
		}
		// auto-insert start node (will be on left-most column as Default Error Handler, if present)
		this.nodes.unshift({ name: Graph.START, origName: "Start", targets: [startNodeNames[0]] });
		this.startNodeNames[0] = Graph.START;

		this.populateNodeSources();
		this.buildGridTree();
		if (!this.compact) {
			this.gridTracker.lastRow--; // compensate for extra row
		}
	}

	// Validates starting nodes and populates sources. First encountered defines tree structure, others are secondary.
	populateNodeSources(): void {
		this.nodes?.forEach(node => {
			node.targets = Array.from(new Set(node.targets));
			node.sources = this.nodes.filter(n => n.targets?.indexOf(node.name) > -1).map(n => n.name);
		});
		const invalidStartNodes = this.startNodeNames.filter(name => {
			const n = this.getNode(name);
			return !n || n.sources?.length || n.targets?.length !== 1;
		}).join();
		if (invalidStartNodes) {
			throw new Error(`Invalid start nodes: ${invalidStartNodes}. Verify the nodes exist, `
				+ `have exactly 1 target each and are not targeted by other nodes: ${JSON.stringify(this.nodes)}!`);
		}
		const unlinkedNodes = this.nodes.filter(n => !n.sources?.length && this.startNodeNames.indexOf(n.name) < 0);
		if (unlinkedNodes.length) {
			throw new Error(`There are disconnected nodes: ${unlinkedNodes.map(n => n.name).join()}! `
				+ `Check node targets: ${JSON.stringify(this.nodes)}`)
		}
	}

	buildGridTree(
		tree: GridTree = this.tree,
		targets: string[] = this.startNodeNames
	): void {
		targets.map(t => this.getNode(t)) // throws
			.filter(node => !tree.node || tree.node.name === node.sources[0])
			.forEach(node => {
				if (!(node.name in this.visitedNodes)) {
					try {
						this.gridTracker.currentCol++;
						this.gridTracker.lastCol = Math.max(this.gridTracker.currentCol + 1, this.gridTracker.lastCol);
						const branch: GridTree = { node, branches: [] };
						this.visitedNodes[node.name] = branch;
						tree.branches.push(branch);
						this.buildGridTree(branch, node.targets); // depth-first
					} finally {
						this.gridTracker.currentCol--;
					}
				}
			});
		if (!tree.node) { // tree root's empty node won't be displayed
			return;
		}
		// Rows and columns will be populated from leaves to root (depth first).
		tree.rootCol = Math.max(tree.rootCol || 0, this.gridTracker.currentCol);
		if (!tree.branches?.length) { // leaf (no children); put on lastRow and increment it for the next leaf
			tree.rootRow = this.gridTracker.lastRow++;

			if (!this.compact) {
				this.gridTracker.lastRow++; // extra row
			}
			tree.branches = null;
		} else { // has children - put on row approximately between the first and last child's;
			tree.rootRow = Math.floor((tree.branches[0].rootRow + tree.branches[tree.branches.length - 1].rootRow) / 2); //tree.branches[0].rootRow;//
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
	 * (Re-)calculates x and y coordinates of all nodes based on their branch root's row and column.
	 *
	 * @param {number} [nodeSpacing = 120] - The horizontal spacing between the nodes (vertical spacing is half of it)
	 * @param {number} [height = 600] - The max height of the graph
	 * @param {number} [width = 6000] - The max width of the graph
	 * @param {boolean} [throwOutOfBoundsError = false] - whether to throw an Error when the dimentions exceed the given maximum values (true)
	 * or only logs the error on the console (false)
	 * @returns the graph with recalculated node coordinates as per horizonatal and vertical spacing, with initial offset - half the node spacing.
	 * @throws Error when throwOutOfBoundsError = true and the dimentions exceed the given maximum values
	 */
	setDimensions(
		nodeSpacing: number = 120,
		height: number = 600,
		width: number = 6000,
		throwOutOfBoundsError = false
	): this {
		const gridDimensions = [this.gridTracker.lastCol, this.gridTracker.lastRow].map(d => nodeSpacing * (d + 1));
		const canvasDimensions = [width, height];
		if (gridDimensions[0] > canvasDimensions[0] || gridDimensions[1] > canvasDimensions[1]) {
			const err = `Grid dimensions ${gridDimensions} exceed canvas size ${canvasDimensions}!`;
			if (throwOutOfBoundsError) {
				throw new Error(err);
			} else {
				console.error(err);
			}
		}
		this.treeWalk(this.tree, t => {
			if (t.node) {
				t.node.x = Math.round(nodeSpacing * (0.5 + t.rootCol));
				t.node.y = Math.round(nodeSpacing / 2 * (1 + t.rootRow));
			}
		});

		return this;
	}

	private treeWalk(tree: GridTree, fnPre: (t: GridTree) => void, fnPost: (t: GridTree) => void = (t) => { }) {
		fnPre(tree);
		tree.branches?.forEach(branch => this.treeWalk(branch, fnPre, fnPost));
		fnPost(tree);
	}

	/**
	 * Use to draw a graph in the console
	 *
	 * @WARN: Used for debugging purposes
	 */
	public draw(label: string = "Workflow diagram"): this {
		const blankRow: string[] = Array.from({ length: this.gridTracker.lastCol }, () => "");
		this.treeWalk(this.tree, (b) => {
			if (!b.node || b.rootCol < 0) {
				return;
			}
			blankRow[b.rootCol] = blankRow[b.rootCol].padEnd(
				Math.max(blankRow[b.rootCol].length, 5 + b.node.name.length + b.node.origName.length)
			)
		});
		const grid = Array.from({ length: this.gridTracker.lastRow }, () => [...blankRow]);
		const getSiblings = (branch: GridTree) => this.visitedNodes[branch.node?.sources[0]]?.branches || [];
		const getPrefix = (branch: GridTree) => branch.rootCol === 0 ? ">[" // root
			: (getSiblings(branch).length === 1 ? "-[" // only branch
				: (getSiblings(branch)[0] === branch ? "/[" // first branch
					: (getSiblings(branch)[getSiblings(branch).length - 1] === branch ? "\\[" // last branch
						: "|["))) // middle branch
		const getSuffix = (branch: GridTree) => !branch.branches?.length ? "] " : "]-";
		this.treeWalk(this.tree, (branch: GridTree) => {
			if (!branch.node || branch.rootCol < 0) {
				return;
			}
			let nodeStr = `${branch.node.name}:${branch.node.origName}`;
			const maxNodeNameLength = blankRow[branch.rootCol].length - 4;
			nodeStr = nodeStr.padStart(nodeStr.length + Math.floor((maxNodeNameLength - nodeStr.length) / 2));
			grid[branch.rootRow][branch.rootCol] = `${getPrefix(branch)}${nodeStr.padEnd(maxNodeNameLength)}${getSuffix(branch)}`;
		});

		const rows = grid.map(line => line.join(''));
		const border = Array(rows[0]?.length || 0).fill("-").join("");

		const omittedConnections = [];
		this.treeWalk(this.tree, (branch) => {
			if (!branch.node || branch.rootCol <= 0) {
				return;
			}
			omittedConnections.push(...(branch.node.sources || []).filter((src, ind) => !!ind).map(src => `${src} -> ${branch.node.name}`));
		});
		if (omittedConnections.length) {
			omittedConnections.unshift("Secondary connections (not displayed):");
		}

		console.debug([label, border, ...rows, border, ...omittedConnections].join('\n'));
		return this;
	}

	public getNode(name: string): GraphNode {
		const node = this.nodes.find(node => node.name === name);
		if (!node) {
			throw new Error(`Node "${name}" not found`);
		}

		return node;
	}
}
