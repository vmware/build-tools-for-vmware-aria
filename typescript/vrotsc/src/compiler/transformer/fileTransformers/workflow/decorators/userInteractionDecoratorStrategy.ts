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
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import BaseItemDecoratorStrategy from "./base/baseItemDecoratorStrategy";
import { GraphNode } from "./helpers/graph";
import { InputOutputBindings } from "./helpers/presentation";

/**
 * Responsible for printing an user interaction component. Note that the following binding must be present:
 * 1. security.group (variable name: security_group),
 * 2. security.assignees (variable name: security_assignees)
 * 3. security.assignee.group (variable name: security_assignee_groups)
 * 4. timeout.date (variable name: timeout_date)
 * @example
 * ```xml
 <workflow-item name="item3" out-name="item2" type="input">
    <display-name><![CDATA[User interaction]]></display-name>
    <in-binding>
      <bind name="security.group" type="LdapGroup" export-name="security_group">
        <description><![CDATA[Any user member of this group will be authorized to fill in this form.]]></description>
      </bind>
      <bind name="security.assignees" type="Array/LdapUser" export-name="security_assignees">
        <description><![CDATA[Any user from this array of users will be authorized to fill in this form]]></description>
      </bind>
      <bind name="security.assignee.groups" type="Array/LdapGroup" export-name="security_assignee_groups">
        <description><![CDATA[Any user member of any of the groups will be authorized to fill in this form.]]></description>
      </bind>
      <bind name="timeout.date" type="Date" export-name="timeout_date">
        <description><![CDATA[If not null, this input item will wait until date and will continue workflow execution.]]></description>
      </bind>
    </in-binding>
    <out-binding>
      <bind name="outputVariable" type="number" export-name="outputVariable"/>
    </out-binding>
    <presentation/>
    <description><![CDATA[User interaction component]]></description>
    <position y="50.0" x="280.0"/>
  </workflow-item>
 * ```
 */
export default class UserInteractionDecoratorStrategy extends BaseItemDecoratorStrategy {
    public itemPosition: number;

    /**
     * Return XML tag for the user interaction component.
     *
     * @returns XML tag name.
     */
    public getCanvasType(): string {
        return "input";
    }

    /**
     * Return the workflow item type supported by this decorator.
     *
     * @returns type of the workflow element.
     */
    public getDecoratorType(): WorkflowItemType {
        return WorkflowItemType.UserInteractionItem;
    }

    public getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number): GraphNode {
        this.itemPosition = pos;
        return super.getGraphNode(itemInfo, pos);
    }

    /**
     * Prints out the default handler item. Note that it needs to be connected with an end item and
     * both must have identical name.
     *
     * @param itemInfo The item to print.
     * @param pos The position of the item in the workflow.
     * @param x position on X axis that will be used for UI display
     * @param y position on Y axis that will be used for UI display
     *
     * @returns The string representation of the item.
     */
    public printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
        const targetItem = super.findTargetItem(itemInfo.target, pos, itemInfo);
        if (targetItem === null) {
            throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
        }

        const stringBuilder = new StringBuilderClass("", "");
        stringBuilder.append(`<workflow-item`
            + ` name="item${pos}"`
            + ` out-name="${targetItem}"`
            + ` type="${this.getCanvasType()}" `
        );

        if (itemInfo.canvasItemPolymorphicBag.exception) {
            stringBuilder.append(` catch-name="${super.findTargetItem(itemInfo.canvasItemPolymorphicBag.exception, pos, itemInfo)}" `);
        }

        stringBuilder.append("> ");
        stringBuilder.append(` <display-name><![CDATA[${itemInfo.name}]]></display-name> `).appendLine();
        stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.IN_BINDINGS));
        stringBuilder.appendContent(super.buildParameterBindings(itemInfo, InputOutputBindings.OUT_BINDINGS));
        stringBuilder.append(super.formatItemPosition([x, y])).appendLine();
        stringBuilder.append(` <presentation/> `).appendLine();
        stringBuilder.unindent();
        stringBuilder.append(` </workflow-item>`).appendLine();

        return stringBuilder.toString();
    }

    /**
     * Return the itemId as it will appear in the XML file.
     *
     * @returns String representation of the item Id (i.e. item3)
     */
    public getItemId(): string {
        return `item${this.itemPosition}`;
    }
}
