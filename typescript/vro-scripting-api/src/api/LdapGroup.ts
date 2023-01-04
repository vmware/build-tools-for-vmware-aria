namespace vroapi {
    /**
     * vRO LdapGroup intrinsic class representation
     */
    export class LdapGroup {

        displayName: string;

        commonName: string;

        displayInfo: string;

        dn: string;

        parentGroups: LdapGroup[] = [];

        subGroups: LdapGroup[] = [];

        users: LdapUser[] = [];

        emailAddress: string;

    }

    global.LdapGroup = LdapGroup as any;
}
