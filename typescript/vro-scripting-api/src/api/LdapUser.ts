namespace vroapi {
    /**
     * vRO LdapUser intrinsic class representation
     */
    export class LdapUser {

        commonName: string;

        userPrincipalName: string;

        displayName: string;

        loginName: string;

        dn: string;

        displayInfo: string;

        groups: LdapGroup[] = [];

        emailAddress: string;

        allGroups: LdapGroup[] = [];

        /**
         * @param ldapGroup
         */
        isMemberOfGroup(ldapGroup: any): boolean {
            throw new NotSupportedError();
        }

    }

    global.LdapUser = LdapUser as any;
}
