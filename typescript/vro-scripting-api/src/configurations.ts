/*
 * #%L
 * vro-scripting-api
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
namespace vroapi {
    export const configurations = {
        getRootCategories,
        getCategories,
        getCategory,
        removeCategory,
        getElements,
        getElement,
        createElement,
        removeElement,
        getElementAttributes,
    };

    interface CategoryDescriptor {
        name: string;
        // Path to category in the tree.
        path: string;
        description?: string;
        elements: Record<string, ElementDescriptor>;
        children: Record<string, CategoryDescriptor>;
        instance?: ConfigurationElementCategory;
        parent: CategoryDescriptor;
    }

    interface ElementDescriptor {
        name: string;
        description?: string;
        version?: string;
        // Path to JSON file describing config attributes.
        path?: string;
        instance?: ConfigurationElement;
    }

    interface AttributeDescriptor {
        name: string;
        description: string;
        type: string;
        value: string;
        readOnly?: boolean;
    }

    const fs: typeof import("fs-extra") = require("fs-extra");
    const MAPPING_FILE_NAME = "vro-configurations.json";
    let _root: CategoryDescriptor;

    function getRoot(): CategoryDescriptor {
        return _root || (_root = {
            children: buildCategoryTree(fs.existsSync(MAPPING_FILE_NAME) ? parseJsonFile(MAPPING_FILE_NAME) : {}),
        } as CategoryDescriptor);
    }

    function getRootCategories(): ConfigurationElementCategory[] {
        return Object.values(getRoot().children || {}).map(child => getOrCreateCategory(child));
    }

    function getCategories(categoryPath: string): ConfigurationElementCategory[] {
        const parentDescriptor = findDescriptorByPath(categoryPath);
        return Object.values(parentDescriptor.children || {}).map(child => getOrCreateCategory(child));
    }

    function getCategory(categoryPath: string): ConfigurationElementCategory {
        const categoryDescriptor = findDescriptorByPath(categoryPath);
        return categoryDescriptor ? getOrCreateCategory(categoryDescriptor) : null;
    }

    function removeCategory(categoryPath: string) {
        const path = categoryPath.split("/");
        const name = path.pop();
        const parentDescriptor = path.length ? findDescriptorByPath(path.join("/")) : getRoot();
        delete parentDescriptor.children[name];
        if (parentDescriptor.instance) {
            parentDescriptor.instance.invalidateSubCategories();
        }
    }

    function getElements(categoryPath: string): ConfigurationElement[] {
        const categoryDescriptor = findDescriptorByPath(categoryPath);
        if (!categoryDescriptor) {
            return [];
        }
        const category = getOrCreateCategory(categoryDescriptor);
        return Object.values(categoryDescriptor.elements).map(elem => getOrCreateElement(elem, category));
    }

    function getElement(categoryPath: string, name: string): ConfigurationElement {
        const categoryDescriptor = findDescriptorByPath(categoryPath);
        if (!categoryDescriptor) {
            return null;
        }
        const category = getOrCreateCategory(categoryDescriptor);
        const elementDescriptor = categoryDescriptor.elements[name];
        if (!elementDescriptor) {
            return null;
        }
        return getOrCreateElement(elementDescriptor, category);
    }

    function createElement(categoryPath: string, name: string): ConfigurationElement {
        const categoryDescriptor = categoryPath.split("/").reduce((parent, name) => {
            let child: CategoryDescriptor = parent.children[name];
            if (!child) {
                child = {
                    name,
                    path: parent.path ? `${parent.path}/${name}` : name,
                    parent,
                    elements: {},
                    children: {},
                };
                parent.children[name] = child;
            }
            return child;

        }, getRoot());
        const category = getOrCreateCategory(categoryDescriptor);
        category.invalidateElements();
        const elementDescriptor = { name } as ElementDescriptor;
        categoryDescriptor.elements[name] = elementDescriptor;
        return getOrCreateElement(elementDescriptor, category);
    }

    function removeElement(categoryPath: string, name: string) {
        const categoryDescriptor = findDescriptorByPath(categoryPath);
        if (!categoryDescriptor) {
            throw new Error(`Configuration category with path '${categoryPath}' does not exist.`);
        }
        delete categoryDescriptor.elements[name];
        if (categoryDescriptor.instance) {
            categoryDescriptor.instance.invalidateElements();
        }
    }

    function getElementAttributes(categoryPath: string, elementName: string): Attribute[] {
        const categoryDescriptor = findDescriptorByPath(categoryPath);
        if (!categoryDescriptor) {
          return null;
        }
        const elementDescriptor = categoryDescriptor.elements[elementName];
        if (!elementDescriptor || !elementDescriptor.path) {
          return null;
        }
        const attributeDescriptors = parseJsonFile<AttributeDescriptor[]>(elementDescriptor.path);
        if (attributeDescriptors.length === 0) {
          return null;
        }
        return attributeDescriptors.map(attrInfo => {
          const attr = new Attribute();
          attr.name = attrInfo.name;
          attr.description = attrInfo.description;
          attr.type = attrInfo.type;
          attr.value = attrInfo.value != null ? convertAttrValue(attrInfo.type, attrInfo.value) : null;
          return attr;
        });
      }


    function findDescriptorByPath(categoryPath: string): CategoryDescriptor {
        return categoryPath.split("/").reduce((parent, name) => parent ? parent.children[name] : null, getRoot());
    }

    function getOrCreateCategory(descriptor: CategoryDescriptor): ConfigurationElementCategory {
        if (!descriptor.instance) {
            const category = descriptor.instance = new ConfigurationElementCategory();
            category.name = descriptor.name;
            category.description = descriptor.description;
            category.path = descriptor.path;
            category.parent = descriptor.parent?.instance;
        }
        return descriptor.instance;
    }

    function getOrCreateElement(descriptor: ElementDescriptor, category: ConfigurationElementCategory): ConfigurationElement {
        if (!descriptor.instance) {
            const element = descriptor.instance = new ConfigurationElement();
            element.name = descriptor.name;
            element.description = descriptor.description;
            element.version = descriptor.version;
            element.configurationElementCategory = category;
        }
        return descriptor.instance;
    }

    function buildCategoryTree(categories: Record<string, CategoryDescriptor>, parent?: CategoryDescriptor): Record<string, CategoryDescriptor> {
        for (const [name, category] of Object.entries(categories)) {
            category.name = name;
            category.path = parent ? `${parent.path}/${name}` : name;
            category.parent = parent;
            category.elements = category.elements || {};
            for (const [elemName, element] of Object.entries(category.elements)) {
                element.name = elemName;
            }
            category.children = buildCategoryTree(category.children || {}, category);
        }
        return categories;
    }

    function convertAttrValue(type: string, value: string): any {
        if (value === "__NULL__") {
            return null;
        }
        const arrayPrefix = "Array/";
        if (type.startsWith(arrayPrefix)) {
            return convertAttrArrayValues(type.substring(arrayPrefix.length), value);
        }
        switch (type) {
            case "string":
                return value;
            case "boolean":
                return value.toString() === "true";
            case "number":
                return parseFloat(value.toString());
            default:
                return undefined;
        }
    }

    function convertAttrArrayValues(type: string, value: string): any[] {
        let tmp = value.toString();
        if (tmp.substring(0, 2) === "#{") {
            tmp = tmp.substring(2);
        }
        if (tmp.substring(tmp.length - 2) === "}#") {
            tmp = tmp.substring(0, tmp.length - 2);
        }
        const valPrefix = `#${type}#`;
        return tmp.split(';').map(piece => {
            piece = piece.substring(valPrefix.length);
            piece = piece.substring(0, piece.length - 1);
            return convertAttrValue(type, piece);
        });
    }
}
