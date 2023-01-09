namespace vroapi {
    /**
     * vRO ResourceElement intrinsic class representation
     */
    export class ResourceElement {

        name: string;

        description: string;

        version: string;

        mimeType: string = "application/octet-stream";

        contentSize: number | undefined;

        readonly versionHistoryItems: VersionHistoryItem[] = [];

        resourceElementCategory: ResourceElementCategory;

        private _mimeAttachment: MimeAttachment;

        /**
         * Returns the content converted to a MimeAttachment.
         */
        getContentAsMimeAttachment(): MimeAttachment {
            if (!this._mimeAttachment) {
                this._mimeAttachment = new MimeAttachment();
                this._mimeAttachment.name = this.name;
                this._mimeAttachment.mimeType = this.mimeType;
                console.log(`this.resourceElementCategory.path=${this.resourceElementCategory.path}`);
                console.log(`this.name=${this.name}`);
                this._mimeAttachment.content = resources.getElementContent(this.resourceElementCategory.path, this.name);
            }
            return this._mimeAttachment;
        }

        /**
         * Returns this element's parent category
         */
        getResourceElementCategory(): ResourceElementCategory {
            return this.resourceElementCategory;
        }

        /**
         * Reload resource element
         */
        reload(): void {
            // Since no changes are made to the file system there should be no action here
            // All changes are done in the runtime and stored in memory
        }

        /**
         * Sets the content of this resource element.
         * @param fileName
         * @param mimeType
         */
        setContentFromFile(fileName: string, mimeType: any): void {
            throw new NotSupportedError();
        }

        /**
         * Sets the content of this resource element.
         * @param mimeAttachment
         */
        setContentFromMimeAttachment(mimeAttachment: MimeAttachment): void {
            this._mimeAttachment = mimeAttachment;
            this.mimeType = mimeAttachment.mimeType;
        }

        /**
         * Writes the content to a file.
         * @param fileName
         */
        writeContentToFile(fileName: string): void { }

        /**
         * Saves a change set in the local version repository.
         * This function is available with vRA 8
         */
        saveToVersionRepository(): void { }
    }

    global.ResourceElement = ResourceElement as any;
}
