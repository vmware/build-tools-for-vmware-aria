namespace vroapi {
    /**
     * vRO Regexp intrinsic class representation
     */
    export class Regexp extends RegExp {
    }

    global.Regexp = Regexp as any;
}
