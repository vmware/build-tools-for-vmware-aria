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
 * @brief Prints out arrays
 *
 * @details THIS PRINTS THE ARRAYS IN vRO8 FORMAT
 *          Array in vRO8
 *          [16:string#123312321,14:string#1231323,17:string#qffwfqwfwq]
 *          the number in front is the total length of the "type#value" string
 *
 *          Array in vRO7
 *          #{#field4:string#test#;#field4:string#test2#;#field4:string#test4#}#
 *          WARNING! No longer supported - attributes in this format make the Workflow inaccessible via UI when imported
 *
 * @param value
 * @param type
 */
export function printAttributeArrayValue(value: Array<any>, type: string) {
    const cleanedType = type.replace("Array/", "");

    const output = value.map(element => {
        const typeAndValue = `${cleanedType}#${element}`;
        return `${typeAndValue.length}:${typeAndValue}`;
    }).join(",");

    return `[${output}]`;
}

/**
 * @brief   Prints out composite type values.
 *
 * @details THIS PRINTS THE COMPOSITE TYPE IN vRO7 FORMAT
 *          vRO8 is backwards compatible, but future versions may not be.
 *          Composite type in vRO8: {5:13:field=string#Stefan
 *              7:12:field_1=number#123.0
 *          }
 *
 *          Composite type in vRO7:
 *          #[#field#=#string#Stefan#+#field_1#=#number#123.0#]#
 *          #[#field1#=#string#rabbit#+#field2#=#Array##{#string#Rebecca#;#string#Pedro#;#string#Peppa#}##]#
 *
 * @param   {any} compositeValue
 * @param   {String} compositeType
 * @private
 */
export function printAttributeCompositeValue(compositeValue: any, compositeType: string) {
    const output = Object.entries(compositeValue).map(([key, value]) => (Array.isArray(value)
        ? [key, "Array", printAttributeArrayValue(value as any[], getArrayTypeFromCompositeType(compositeType, key))]
        : [key, typeof value, value]))
        .map(([key, valueType, value]) => `#${key}#=#${valueType}#${value}#`)
        .join("+");

    return `#[${output}]#`;
}

/**
 * @brief   Extracts the Array Type from the Composite Type
 *
 * @details Example CompositeType: CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest
 *          If the key passed is field4 will extract `Array/string`
 *
 * @param   {String} compositeType
 * @param   {String} key
 *
 * @private
 */
export function getArrayTypeFromCompositeType(compositeType: string, key: string): string | null {
    const result = compositeType.match(new RegExp(`${key}:(Array\\/[^,)]+)`))?.[1];
    if (!result) {
        throw new Error(`Composite Type Array is in invalid format for ${key}!`);
    }

    return result;
}
