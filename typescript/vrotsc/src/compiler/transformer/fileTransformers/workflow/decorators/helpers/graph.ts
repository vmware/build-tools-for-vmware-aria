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
 * The node holds information about it's position
 */
export interface GraphNode {
	name: string;
	x?: number;
	y?: number;
	targets: string[];
}

/**
 * Leaf holds the node and it's children(leaves)
 */
export interface Leaf {
	node: GraphNode;
	leaves: Leaf[];
}

/**
 * Tree holds the structure of the graph, it's populated with the calculated positions
 */
interface Tree {
	startNodes: string[];
	leaves: {
		[nodeName: string]: Leaf;
	};
}

/**
 * This is used to chart out a graph and give you the x and y positions of each node
 *
 * Example:
 * ```typescript
const nodes = [
	// First Start
	{ name: "A", targets: ["B"] },
	{ name: "B", targets: ["C"] },
	{ name: "C", targets: ["D", "G"] },
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

	// Third Start
	{ name: "X", targets: ["Y"] },
	{ name: "Y", targets: [] },
];

const graph = new Graph(nodes, ["A", "S", "X"]);

graph.draw();
 * ```
 *
 * Will output:
 * ```
....................................................................................................
....................................................................................................
....................................................................................................
..............................F....O....P....Q......................................................
....................................................................................................
....................................................................................................
....................D....E..........................................................................
....................................................................................................
.....A....B....C...................J................................................................
....................................................................................................
....................................................................................................
....................G....H....I.....................................................................
....................................................................................................
.....S....T....U...................K................................................................
....................................................................................................
....................................................................................................
....................................................................................................
....................................................................................................
...............W...................L................................................................
....................................................................................................
....................................................................................................
....................................................................................................
....................................................................................................
...................................M................................................................
....................................................................................................
....................................................................................................
 * ```
 */
export class Graph {
	/**
	 * Holds a flag if calculations were already done
	 */
	private calculated = false;

	/**
	 * Will hold the calculated tree
	 */
	private tree: Tree;

	/**
	 * @param {GraphNode[]} nodes - List of the nodes that will be used to build the tree
	 * @param {string[]} startNodeNames - List of the names of the nodes that will be used as the start of the tree
	 * @param {number} nodeSpacing - The spacing between the nodes
	 * @param {number} height - The height of the graph
	 * @param {number} width - The width of the graph
	 */
	constructor(
		private nodes: GraphNode[],
		startNodeNames: string[],
		private nodeSpacing: number = 5,
		private readonly height: number = 40,
		private readonly width: number = 100
	) {
		this.tree = {
			startNodes: startNodeNames,
			leaves: {}
		};

		this.calculatePositions();
	}

	/**
	 * Use to draw a graph in the console
	 *
	 * @WARN: Used for debugging purposes
	 */
	public draw() {
		this.calculatePositions();

		const grid = Array.from({ length: this.height }, () => Array(this.width).fill('.'));

		for (const leaf of Object.values(this.tree.leaves)) {
			grid[Math.round(leaf.node.y)][Math.round(leaf.node.x)] = leaf.node.name;
		}

		grid.forEach(line => console.log(line.join('')));
	}

	/**
	 * Retrieves the node by name
	 *
	 * @param {string} name
	 * @returns {GraphNode}
	 * @throws {Error} if the node is not found
	 */
	public getNode(name: string): GraphNode {
		const node = this.nodes.find(node => node.name === name);
		if (!node) {
			throw new Error(`Node "${name}" not found`);
		}

		return node;
	}

	/**
	 * Get the leaf by name
	 *
	 * @param {string} name
	 * @returns {Leaf | null}
	 */
	public getLeaf(name: string): Leaf {
		const leaf = this.tree.leaves[name];
		if (!leaf) {
			throw new Error(`Leaf "${name}" not found`);
		}

		return leaf;
	}

	/**
	 * Height of the graph
	 *
	 * Retrieve from here so you don't have to store a variable
	 */
	public getHeight() {
		return this.height;
	}

	/**
	 * Width of the graph
	 *
	 * Retrieve from here so you don't have to store a variable
	 */
	public getWidth() {
		return this.width;
	}

	/**
	 * Node spacing
	 *
	 * Retrieve from here so you don't have to store a variable
	 */
	public getNodeSpacing() {
		return this.nodeSpacing;
	}

	/**
	 * Check if a node is already at the given position
	 */
	private hasOverlap(x: number, y: number) {
		for (const leaf of Object.values(this.tree.leaves)) {
			if (Math.round(leaf.node.x) === Math.round(x) && Math.round(leaf.node.y) === Math.round(y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Builds the tree
	 *
	 * Calculates the x and y positions of each leaf
	 */
	private buildTree(
		origin: GraphNode,
		visited: Set<GraphNode> = new Set<GraphNode>(),
		x: number = this.nodeSpacing,
		y: number = this.height / 2
	) {
		while (this.hasOverlap(x, y)) {
			y += this.nodeSpacing;
		}

		origin.x = x;
		origin.y = y;

		let positions = {
			x: x + this.nodeSpacing,
			y: y
		};

		if (origin.targets.length > 1) {
			positions.y -= this.nodeSpacing * (origin.targets.length - 1) / 2;
		}

		origin.targets.forEach(targetName => {
			const target = this.getNode(targetName);

			if (!this.tree.leaves[target.name]) {
				this.tree.leaves[target.name] = {
					node: target,
					leaves: []
				};
			}

			const targetLeaf = this.getLeaf(target.name);
			this.getLeaf(origin.name).leaves.push(targetLeaf);

			// LPS (`Loop Prevention System`)
			if (visited.has(target)) return;
			visited.add(target);

			this.buildTree(targetLeaf.node, visited, positions.x, positions.y);
			positions.y += this.nodeSpacing;
		});
	}

	/**
	 * Calculates all the positions of the tree leaves
	 *
	 * If this method is called multiple times, it will only calculate the positions once
	 */
	private calculatePositions() {
		if (this.calculated) return;

		for (const node of this.nodes) {
			this.tree.leaves[node.name] = {
				node,
				leaves: []
			};
		}

		const visited = new Set<GraphNode>();
		const spacedOutY = this.height / (this.tree.startNodes.length + 1);
		const positions = { x: this.nodeSpacing, y: spacedOutY };

		for (const name of this.tree.startNodes) {
			const startNode = this.getNode(name);
			this.buildTree(startNode, visited, positions.x, positions.y);

			positions.y += spacedOutY;
		}

		this.calculated = true;

		for (const leaf of Object.values(this.tree.leaves)) {
			if (this.tree.startNodes.includes(leaf.node.name)) {
				continue;
			}

			if (leaf.node.x === undefined || leaf.node.y === undefined) {

				console.log(
					JSON.stringify(
						Object.values(this.tree.leaves).map(l => {
							return { node: l.node, leaves: l.leaves.map(le => le.node.name) };
						}),
						null,
						2
					)
				);

				throw new Error(`Node "${leaf.node.name}" has no position, seems like one of the nodes is not connected which would make your wf unusable, check your targets. Above should be a log of all the leaves.`);
			}
		}
	}


}
