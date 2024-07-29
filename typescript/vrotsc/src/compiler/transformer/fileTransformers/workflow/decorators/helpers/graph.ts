// const nodes = [
// 	// First Start
// 	{ name: "A", targets: ["B"] },
// 	{ name: "B", targets: ["C"] },
// 	{ name: "C", targets: ["D", "G"] },
// 	{ name: "D", targets: ["E"] },
// 	{ name: "E", targets: ["C", "F"] },
// 	{ name: "F", targets: ["O"] },
// 	{ name: "G", targets: ["H"] },
// 	{ name: "H", targets: ["I"] },
// 	{ name: "I", targets: ["J", "K", "L", "M"] },
// 	{ name: "J", targets: [] },
// 	{ name: "K", targets: [] },
// 	{ name: "L", targets: [] },
// 	{ name: "M", targets: [] },
// 	{ name: "O", targets: ["P"] },
// 	{ name: "P", targets: ["Q"] },
// 	{ name: "Q", targets: [] },
//
// 	// Second start
// 	{ name: "S", targets: ["T"] },
// 	{ name: "T", targets: ["U", "W", "D"] },
// 	{ name: "U", targets: [] },
// 	{ name: "W", targets: [] },
//
// 	// Third Start
// 	{ name: "X", targets: ["Y"] },
// 	{ name: "Y", targets: [] },
// ];
//
// const graph = new Graph(nodes, ["A", "S", "X"]);
//
// graph.draw();

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

export class Graph {
	/**
	 * Space between nodes
	 */
	public NODE_SPACING = 5;

	/**
	 * Max width of the graph
	 */
	public WIDTH = 100;

	/**
	 * Max height of the graph
	 */
	public HEIGHT = 40;

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
	 */
	constructor(
		private nodes: GraphNode[],
		startNodeNames: string[]
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

		const grid = Array.from({ length: this.HEIGHT }, () => Array(this.WIDTH).fill('.'));

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
		x: number = this.NODE_SPACING,
		y: number = this.HEIGHT / 2
	) {
		while (this.hasOverlap(x, y)) {
			y += this.NODE_SPACING;
		}

		origin.x = x;
		origin.y = y;

		let positions = {
			x: x + this.NODE_SPACING,
			y: y
		};

		if (origin.targets.length > 1) {
			positions.y -= this.NODE_SPACING * (origin.targets.length - 1) / 2;
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
			positions.y += this.NODE_SPACING;
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
		const spacedOutY = this.HEIGHT / (this.tree.startNodes.length + 1);
		const positions = { x: this.NODE_SPACING, y: spacedOutY };

		for (const name of this.tree.startNodes) {
			const startNode = this.getNode(name);
			this.buildTree(startNode, visited, positions.x, positions.y);

			positions.y += spacedOutY;
		}

		this.calculated = true;
	}


}
