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
 * The node holds information about its position and linked nodes
 */
export interface GraphNode {
	/** Workfrow item name (item#). Required. */
	name: string;
	/** Workfrow item original name (method name). Required. */
	origName: string;
	/** Item names (item#) of linked workflow elements. Required. */
	targets: string[];
	/** Canvas X coordinate (calculated) */
	x?: number;
	/** Canvas Y coordinate (calculated) */
	y?: number;
	/**
	 * Names of nodes that have the current node name as target (calculated)
	 * First one is the primary source, which defines the tree structure.
	 */
	sources?: string[];
	/** Names of target nodes for which the current node is the primary source (calculated) */
	branches?: string[];
	/** Grid row (calculated) */
	row?: number;
	/** Grid column (calculated) */
	col?: number;
}

/**
 * Holds the dimensions of the grid.
 * Tracks the grid position while the Graph is being populated.
 */
interface GridTracker {
	/** Last reached row index per column. Length of array is total № of columns */
	lastRowIndPerCol: number[];
	/** Total number of rows */
	totalRows: number;
	/** Tracks tree level (current column) during recursion */
	currentCol?: number;
}

/**
 * Wrapper for an array of {@link GraphNode}s (with names and targets),
 * which on initialization populates the nodes' canvas positions (x,y),
 * grid positions (row, col) and related nodes (sources, branches)
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
 * Node coordinates will [x,y] be calculated based on grid coordinates, node spacing and initial offset.
 * Example:
```
const nodes = [
	{name: "start", origName: "Start", targets: ["item6","item9"]}, // default start; targets the root and default error handler items
	{name: "item1", origName: "decisionElement", targets: ["item7", "item2"]},
	{name: "item2", origName: "prepareItems", targets: ["item3"]},
	{name: "item3", origName: "callOtherWf", targets: ["item4"]},
	{name: "item4", origName: "print", targets: ["item0"]},
	{name: "item5", origName: "execute", targets: ["item1"]},
	{name: "item6", origName: "prepare", targets: ["item5"]}, // root item
	{name: "item7", origName: "waitForEvent", targets: ["item5"]},
	{name: "item8", origName: "workflowEnd", targets: []}, // secondary end
	{name: "item9", origName: "defaultErrorHandler", targets: ["item8"]}, // default error handler (secondary start)
	{name: "item0", origName: "End0", targets: []}, // default end
]

new Graph(nodes).build().calculateCanvasPositions().draw();
```
* Will output diagram of the type:
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
	 * Will hold grid dimensions after the tree is calculated;
	 * used during tree calculation to track current row/column index.
	 */
	private gridTracker: GridTracker;

	/**
	 * Record of all visited nodes during tree calculation. Object keys are the node names.
	 * Will hold all connected nodes at the end of tree calculation or be set to null on failure.
	 */
	private visitedNodes: Record<string, GraphNode>;

	/**
	 * @param {GraphNode[]} nodes - List of the nodes that will be used to build the tree.
	 * Must contain a Start node with name={@link START 'start'} with at least one target node.
	 * The first target will be displayed as its child.
	 * Note: Any remaining targets will be displayed as secondary starting elements (at the same level as the default Start)
	 */
	constructor(public readonly nodes: GraphNode[] = []) { }

	/**
	 * Populates node sources and branches based on targets.
	 * First encountered source defines tree structure, others are secondary.
	 * Calculates grid dimensions.
	 * @throws Error if a targeted node doesn't exist
	 * @throws Error if there are disconnected nodes
	 */
	public build(): this {
		this.gridTracker = { currentCol: 0, lastRowIndPerCol: [], totalRows: 0 };
		this.visitedNodes = {};
		this.populateNodeSources();
		try {
			this.buildGraphNode(this.getNode(Graph.START));
		} catch (err) {
			this.visitedNodes = null;
			throw err;
		}

		return this;
	}

	/**
	 * Calculates node canvas positions.
	 * @param {[number, number]} [nodeSpacing = [100, 50]] - horizontal and vertical spacing in canvas units between adjacent nodes
	 * @param {[number, number]} [initialOffset = [0, 20]] - horizontal and vertical initial offset in canvas units
	 * @param {[number, number]} [maxGridDimensions = [10, 50]] - maximum number of grid rows and columns
	 * @returns {GraphNode} the Graph with updated node positions, where:
	 * node.x = node.col * nodeSpacing[0] + initialOffset[0]
	 * node.y = node.row * nodeSpacing[1] + initialOffset[1]
	 * @throws Error if grid isn't built
	 * @throws Error if grid exceeds the maximum size
	 */
	public calculateCanvasPositions(
		nodeSpacing: [number, number] = [100, 50],
		initialOffset: [number, number] = [0, 20],
		maxGridDimensions: [number, number] = [10, 50]
	): this {
		if (!this.visitedNodes) {
			throw new Error(`The graph needs to be built first!`);
		}

		const gridDimensions = [this.gridTracker.totalRows, this.gridTracker.lastRowIndPerCol.length];
		if (gridDimensions[0] > maxGridDimensions[0] || gridDimensions[1] > maxGridDimensions[1]) {
			throw new Error(`Grid dimensions ${gridDimensions} exceed the allowed number of rows/colums ${maxGridDimensions}!`);
		}
		this.nodes.forEach(node => {
			node.x = nodeSpacing[0] * node.col + initialOffset[0];
			node.y = nodeSpacing[1] * node.row + initialOffset[1];
		});

		return this;
	}

	/**
	 * Populates node sources based on all node targets.
	 * @throws Error if a targeted node doesn't exist
	 * @throws Error if there are disconnected nodes
	 */
	private populateNodeSources() {
		this.nodes?.forEach(node => {
			node.branches = [];
			node.sources = this.nodes.filter(n => n.targets?.indexOf(node.name) > -1).map(n => n.name);
		});
		const unlinkedNodes = this.nodes.filter(n => !n.sources?.length && n.name !== Graph.START);
		if (unlinkedNodes.length) {
			throw new Error(`There are disconnected nodes: ${unlinkedNodes.map(n => `${n.name}(${n.origName})`).join()}!`);
		}
	}

	/**
	 * Populates the branches for the node based on its targets.
	 * Called recursively for each branch before calculating the node's grid position
	 * (Rows and columns will be populated from leaves to root - depth first).
	 * Uses gridTracker.currentCol to track node column and the level of recursion
	 * @param {GraphNode} treeNode - node to recursively update.
	 */
	protected buildGraphNode(treeNode: GraphNode): void {
		treeNode.targets.map(t => this.getNode(t)) // throws
			.filter(branch => !(branch.name in this.visitedNodes) && branch.sources[0] === treeNode.name)
			.forEach(branch => {
				// secondary start nodes will be placed in col. 0:
				const colIncrement = this.getNode(Graph.START).targets.indexOf(branch.name) > 0 ? 0 : 1;
				try {
					this.gridTracker.currentCol += colIncrement;
					this.visitedNodes[branch.name] = branch;
					treeNode.branches.push(branch.name);
					this.buildGraphNode(branch); // depth-first
				} finally {
					this.gridTracker.currentCol -= colIncrement;
				}
			});

		treeNode.col = this.gridTracker.currentCol;
		this.calculateGridRow(treeNode);
	}

	/**
	 * Calculates a node's grid row:
	 * If the node is leaf (no children) - puts it on the last free row (with 1 row for spacing between sibling nodes).
	 * If the node has children, the row is the average of the first and list child's.
	 * While there is a successor node on the same row back-referencing it, it is moved to the next row.
	 * @param {GraphNode} treeNode - node whose grid position is being updated
	 */
	private calculateGridRow(treeNode: GraphNode) {
		if (!treeNode.branches?.length) { // leaf (no children); put on lastRow and increment it for the next leaf
			treeNode.row = this.getNextRow(treeNode.col);
			treeNode.branches = null;
		}
		else if (treeNode.name === Graph.START) { // start node is on same row as first branch
			treeNode.row = this.getNode(treeNode.branches[0]).row;
		}
		else { // branch has children - put on row approximately between the first and last child's;
			treeNode.row = Math.floor((this.getNode(treeNode.branches[0]).row + this.getNode(treeNode.branches[treeNode.branches.length - 1]).row) / 2);
			// in case of a node on the same row back referencing this one - avoid arrows crossing intermediate node(s)
			while (
				treeNode.sources.find((src, ind) => !!ind && (src in this.visitedNodes)  // visited, but not parent
					&& this.getNode(src).row === treeNode.row && this.getNode(src).col > treeNode.col + 1) // further than next col on same row
			) {
				treeNode.row++;
			}
		}
		this.gridTracker.lastRowIndPerCol[treeNode.col] = treeNode.row;
		this.gridTracker.totalRows = Math.max(this.gridTracker.totalRows, 1 + treeNode.row);
	}

	/**
	 * Finds a node with the given name. Checks visited nodes first.
	 * @param {string} name
	 * @returns {GraphNode}
	 * @throws Error if the Graph contains no node with the given name.
	 */
	public getNode(name: string): GraphNode {
		const node = this.visitedNodes[name] || this.nodes.find(node => node.name === name);
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
		// blank grid
		const blankRow: string[] = Array.from(this.gridTracker.lastRowIndPerCol, () => "");
		this.nodes.forEach((node) => {
			blankRow[node.col] = blankRow[node.col].padEnd(
				Math.max(blankRow[node.col].length, 5 + node.name.length + node.origName.length)
			)
		});
		const grid = Array.from({ length: this.gridTracker.totalRows }, () => [...blankRow]);

		// helper functions
		const getSiblings = (branch: GraphNode) => this.getNode(branch.sources[0])?.branches || [];
		const getPrefix = (branch: GraphNode) => branch.col === 0 ? ">[" // root
			: (branch.col === 1 || getSiblings(branch).length === 1 ? "-[" // only branch (incl. from start)
				: (getSiblings(branch)[0] === branch.name ? "/[" // first branch
					: (getSiblings(branch)[getSiblings(branch).length - 1] === branch.name ? "\\[" // last branch
						: "|["))) // middle branch
		const getSuffix = (branch: GraphNode) => !branch.branches?.length ? "] " : "]-";

		// populate grid
		this.nodes.forEach((branch: GraphNode) => {
			let nodeStr = `${branch.name}:${branch.origName}`;
			const maxNodeNameLength = blankRow[branch.col].length - 4;
			nodeStr = nodeStr.padStart(nodeStr.length + Math.floor((maxNodeNameLength - nodeStr.length) / 2));
			grid[branch.row][branch.col] = `${getPrefix(branch)}${nodeStr.padEnd(maxNodeNameLength)}${getSuffix(branch)}`;
		});

		// connections not shown
		const omittedConnections = this.nodes
			.map(node => node.sources.filter((src, ind) => !!ind).map(src => `${src} -> ${node.name}`))
			.reduce((res, arr) => [...res, ...arr], []);
		if (omittedConnections.length) {
			omittedConnections.unshift("Secondary connections (not displayed):");
		}


		// print grid with label, border and omitted connections
		const rows = grid.map(line => line.join(''));
		const border = Array(rows[0]?.length || 0).fill("-").join("");
		console.debug([label, border, ...rows, border, ...omittedConnections].join('\n'));
		return this;
	}

	/**
	 * Calculates the next available row for a leaf node.
	 * Checks visited nodes up to its column (+ additional spacing).
	 * Returns the row following the maximum row of the checked nodes (+ additional spacing)
	 * @param {number} col - leaf node column
	 * @param {number} [additional = 1] - additional spacing in rows/columns
	 * @returns {number} row to place the leaf node on
	 */
	private getNextRow(col: number, additional: number = 1): number {
		const currentInd = this.gridTracker.lastRowIndPerCol.reduce(
			(res, val, ind) => Math.max(res, ind > col + additional || typeof val === "undefined" ? -1 : val),
			-1
		);
		return currentInd < 0 ? 0 : (1 + additional + currentInd);
	}
}
