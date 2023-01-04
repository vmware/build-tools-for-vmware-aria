namespace vroapi {
    /**
     * vRO ResourceElementCategory intrinsic class representation
     */
    export class ResourceElementCategory {

        name: string;

        description: string;

        path: string;

        parent: ResourceElementCategory | undefined;

        private _resourceElements: ResourceElement[];
        get resourceElements(): ResourceElement[] {
            return this._resourceElements || (this._resourceElements = resources.getElements(this.path));
        }

        get allResourceElements(): ResourceElement[] {
            return this.resourceElements;
        }

        private _subCategories: ResourceElementCategory[];
        get subCategories(): ResourceElementCategory[] {
            return this._subCategories || (this._subCategories = resources.getCategories(this.path));
        }

        invalidateElements() {
            this._resourceElements = null;
        }

        invalidateSubCategories() {
            this._subCategories = null;
        }
    }

    global.ResourceElementCategory = ResourceElementCategory as any;
}
