import * as ts from 'typescript';
import { Visitor } from '../types';

/**
 * Class representing a NodeVisitor.
 *
 * Brief:
 * This class is used to visit nodes in the TypeScript AST. It provides methods
 * for visiting a single node, multiple nodes, and each child of a node. It also
 * provides methods for getting the parent of a node and checking if a node has
 * parents of certain kinds.
 *
 * Details:
 * Visiting nodes in an Abstract Syntax Tree (AST) is a common technique used in compilers and interpreters for various purposes such as:
 * 1. **Code Transformation**: By visiting each node, you can apply transformations to the code.
 *      This is useful in scenarios like transpiling code from one language (or language version) to another, or optimizing the code for performance.
 * 2. **Code Analysis**: Visiting nodes can be used to analyze the code for various
 *      purposes like linting (checking for stylistic issues), type checking, finding references and dependencies, etc.
 * 3. **Code Generation**: After all transformations and optimizations have been done,
 *      a compiler needs to traverse the AST to generate the target code.
 * In the context of TypeScript, visiting nodes is often used for transforming TypeScript code into
 *      JavaScript code, performing type checking, and other static analyses.
 */
export class NodeVisitor implements Visitor {
	private nodeHeritage: ts.Node[] = [];


    /**
     * Creates a new NodeVisitor.
     *
     * @param {ts.Visitor} callback - The callback to be used when visiting nodes.
     * @param {ts.TransformationContext} context - The context for the transformation.
     */
	constructor(private callback: ts.Visitor, private context: ts.TransformationContext) { }

    /**
     * Visits a single node in the AST.
     *
     * @param {ts.Node} node - The node to visit.
     * @returns {ts.VisitResult<ts.Node>} - The result of visiting the node.
     */
	visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
		this.nodeHeritage.push(node);
		try {
			const result = this.callback(node);
			if (result !== undefined) {
				return result;
			}
			return ts.visitEachChild(node, this.visitNode.bind(this), this.context);
		}
		finally {
			this.nodeHeritage.pop();
		}
	}

    /**
     * Visits multiple nodes in the AST.
     *
     * @param {ts.NodeArray<T> | undefined} nodes - The nodes to visit.
     * @returns {ts.NodeArray<T>} - The result of visiting the nodes.
     */
	visitNodes<T extends ts.Node>(nodes: ts.NodeArray<T> | undefined): ts.NodeArray<T> {
		return ts.visitNodes(nodes, this.visitNode.bind(this));
	}

    /**
     * Visits each child of a node in the AST.
     *
     * @param {T} node - The node whose children to visit.
     * @returns {T} - The result of visiting the children.
     */
	visitEachChild<T extends ts.Node>(node: T): T {
		return ts.visitEachChild(node, this.visitNode.bind(this), this.context);
	}

    /**
     * Gets the parent of a node in the AST.
     *
     * @param {number} [index] - The index of the parent to get.
     * @returns {ts.Node} - The parent of the node.
     */
	getParent(index?: number): ts.Node {
		return this.nodeHeritage[this.nodeHeritage.length - 2 - (index || 0)];
	}

    /**
     * Checks if a node has parents of certain kinds.
     *
     * @param {...ts.SyntaxKind[]} kinds - The kinds of parents to check for.
     * @returns {boolean} - True if the node has parents of the specified kinds, false otherwise.
     */
	hasParents(...kinds: ts.SyntaxKind[]): boolean {
		for (let i = 0; i < kinds.length; i++) {
			const parentNode = this.nodeHeritage[this.nodeHeritage.length - 2 + i];
			if (!parentNode || parentNode.kind !== kinds[i]) {
				return false;
			}
		}
		return true;
	}
}

export function createVisitor(callback: ts.Visitor, context: ts.TransformationContext): Visitor {
	return new NodeVisitor(callback, context);
}
