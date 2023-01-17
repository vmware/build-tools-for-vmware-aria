/*
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");

    export function createVisitor(callback: ts.Visitor, context: ts.TransformationContext): Visitor {
        const nodeHeritage: ts.Node[] = [];

        return {
            visitNode,
            visitNodes,
            visitEachChild,
            getParent,
            hasParents,
        };

        function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
            nodeHeritage.push(node);
            try {
                const result = callback(node);
                if (result !== undefined) {
                    return result;
                }
                return ts.visitEachChild(node, visitNode, context);
            }
            finally {
                nodeHeritage.pop();
            }
        }

        function visitNodes<T extends ts.Node>(nodes: ts.NodeArray<T> | undefined): ts.NodeArray<T> {
            return ts.visitNodes(nodes, visitNode);
        }

        function visitEachChild<T extends ts.Node>(node: T): T {
            return ts.visitEachChild(node, visitNode, context);
        }

        function getParent(index?: number): ts.Node {
            return nodeHeritage[nodeHeritage.length - 2 - (index || 0)];
        }

        function hasParents(...kinds: ts.SyntaxKind[]): boolean {
            for (let i = 0; i < kinds.length; i++) {
                const parentNode = nodeHeritage[nodeHeritage.length - 2 + i];
                if (!parentNode || parentNode.kind !== kinds[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
