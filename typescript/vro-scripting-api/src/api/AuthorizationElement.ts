namespace vroapi {
    /**
     * vRO AuthorizationElement intrinsic class representation
     */
    export class AuthorizationElement {

        name: string;

        description: string;

        ldapElementDn: string;

        ldapElement: LdapGroup | undefined;

        status: string;

        references: AuthorizationReference[] = [];

        /**
         * add an authorization reference on any inventory object and return it
         * @param object
         * @param relation
         */
        addReference(object: any, relation: any): AuthorizationReference {
            throw new NotSupportedError();
        }

        /**
         * remove all reference in this authorization element
         */
        removeAllReferences(): void {
            throw new NotSupportedError();
        }

        /**
         * remove all reference for a given object and relation.
         * @param jsObject
         * @param relation
         */
        removeReference(jsObject: any, relation: any): void {
            throw new NotSupportedError();
        }

    }

    global.AuthorizationElement = AuthorizationElement;
}
