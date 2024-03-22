import { CommonModule } from "@angular/common";
import { Inject, NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { Store } from "@ngrx/store";
import { 
    EXTENSION_ASSET_URL,
    VcdApiClient,
    VcdSdkModule,
    EXTENSION_ROUTE,
    ExtensionNavRegistration,
    PluginModule
} from "@vcd/sdk";
import { ClarityModule } from "@clr/angular";
import { ${projectHeading}Component } from "./${projectName}.component";
import { SampleService } from "./services/sample.service";
import { I18nModule, TranslationService } from "@vcd/i18n";

const ROUTES: Routes = [
    {
        path: "",
        component: ${projectHeading}Component
    }
];

@NgModule({
    imports: [
        ClarityModule,
        CommonModule,
        I18nModule.forChild(EXTENSION_ASSET_URL, true),
        VcdSdkModule.forRoot(),
        FormsModule,
        HttpClientModule,
        VcdSdkModule,
        RouterModule.forChild(ROUTES)
    ],
    declarations: [
        ${projectHeading}Component,
    ],
    bootstrap: [${projectHeading}Component],
    exports: [],
    providers: [
        VcdApiClient,
        SampleService
    ],
})
export class ${projectHeading}Module extends PluginModule {
    constructor(
        appStore: Store<any>,
        @Inject(EXTENSION_ROUTE) extensionRoute: string,
        translationService: TranslationService
    ) {
        super(appStore);

        let registrations: ExtensionNavRegistration[] = [
            {
                path: `${extensionRoute}`,
                nameCode: "${projectName}.nav.label",
                descriptionCode: "${projectName}.nav.description"
            }
        ];
        translationService.registerTranslations();
        registrations.forEach(registration => this.registerExtension(registration));
    }
}
