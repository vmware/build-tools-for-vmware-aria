import { Configuration } from "vrotsc-annotations";


@Configuration({
    name: "ConfigurationEl",
    path: "Some/Path/To",
    attributes: {
        variable1:
        {
            "type": "string",
            "value": "SomeValue",
            "description": "Some Description"
        }
    }
})

export class TestConfiguration { }

