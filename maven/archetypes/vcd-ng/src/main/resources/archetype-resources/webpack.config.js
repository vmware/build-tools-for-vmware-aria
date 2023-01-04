// This is the webpack configuration used for creating plugins.
const webpack = require("webpack");
const path = require("path");
const fs = require("fs-extra");
const xmlParser = require("fast-xml-parser");
const wps = require('webpack-sources');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const Happypack = require('happypack');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const ZipPlugin = require('zip-webpack-plugin');

// All paths are relative to the `webpack.config.js` directory
const basePath = path.resolve(__dirname);
const outPath = path.join(basePath, "target", "bundle");

class VcdPluginVersion {
    apply(compiler) {
        compiler.hooks.emit.tapAsync(VcdPluginVersion.name,
            (compilation, callback) => {
                if (!compilation.compiler.isChild()) {
                    const pomJson = xmlParser.parse(fs.readFileSync("pom.xml").toString());
                    let manifestContent = compilation.assets["manifest.json"].source().toString();
                    manifestContent = manifestContent.replace("VERSION_PLACEHOLDER", pomJson.project.version);
                    compilation.assets["manifest.json"] = new wps.RawSource(manifestContent);
                }
                callback();
            }
        );
    }
}

// Create the configuration for the dev-server
module.exports = {
    // Enable webpack cache.  Not sure what this does, but it's free.
    cache: true,

    // Default to production mode. This is the default anyway, so it
    // just gets rid of a build-time warning
    mode: 'production',

    // Ensure everything is completely relative to the `content/core` directory.
    context: basePath,

    // Use the 'source-map' plugin if source-maps are enabled
    devtool: 'source-map',

    // Write all output to `dist/[name].js` as an AMD library
    output: {
        filename: "[name].js",
        libraryTarget: "amd",
        path: outPath
    },

    // Create 'bundle.js' from 'src/main/index.ts'
    entry: {
        bundle: path.resolve(basePath, "src", "main", "index.ts")
    },

    // Declare everything the container provides as external.
    // Because this is an AMD bundle, they will be resolved via AMD.
    externals: [
        /^rxjs(\/.+)?$/,
        /^@angular\/.+$/,
        /^@clr\/.+$/,
        /^@ngrx\/.+$/,
        /^@vcd\/common$/,
        /^@vcd-ui\/common$/,
        {
            'clarity-angular': 'clarity-angular',
            'reselect': 'reselect'
        }
    ],

    // Resolve typescript first, then Javascript.
    resolve: {
        extensions: [".ts", ".js"]
    },

    module: {
        rules: [
            {
                sideEffects: false
            },
            // Build typescript files using a cache (really useful for subsequent builds).
            // Use the `tsconfig.json` settings.
            //
            // TS files are pre-processed by the `angular2-template-loader` which will inline
            // HTML and CSS for components.
            {
                test: /\.ts$/,
                loaders: [
                    {
                        loader: "awesome-typescript-loader",
                        options: {
                            useCache: true,
                            configFileName: path.join(basePath, "tsconfig.json")
                        }
                    },
                    "angular2-template-loader"
                ]
            },

            // Delegate CSS to Happypack.  See plugins section.
            {
                test: /\.css$/,
                use: 'happypack/loader?id=css'
            },

            // Delegate SCSS to Happypack.  See plugins section.
            {
                test: /\.scss$|\.sass$/,
                use: 'happypack/loader?id=scss'
            },

            // HTML files will become raw strings.
            {
                test: /\.html$/,
                loaders: ["raw-loader"]
            },

            // Fonts encountered by CSS will become files distributed with the project
            {
                test: /\.(ttf|eot|svg|woff|woff2)$/,
                loader: "file-loader?name=fonts/[name].[ext]"
            }
        ]
    },

    plugins: [
        // CSS happypack plugin.  Happypack multithreads the specified loader chain.
        new Happypack({
            id: "css",

            // Apply PostCSS (using an external config - HappyPack requires this) to any CSS file,
            // load it using the css-loader (which is a bit smarter than raw files), before
            // converting it to a string for inclusion in the components directly.
            loaders: [
                "exports-loader?module.exports.toString()",
                {
                    loader: "css-loader",
                    options: {
                        sourceMap: true,
                        importLoaders: 1
                    }
                },
                {
                    loader: "postcss-loader",
                    options: {
                        ident: "postcss",
                        config: {
                            path: path.join(basePath, "postcss.config.js")
                        }
                    }
                }
            ]
        }),

        // SCSS happypack plugin.  Happypack multithreads the specified loader chain.
        new Happypack({
            id: "scss",

            // Apply SASS compiling, then feed the resulting CSS into PostCSS (using an
            // external config - HappyPack requires this) to any SCSS/SASS file.
            // Then load this output CSS using the css-loader (which is a bit smarter than raw files),
            // before converting it to a string for inclusion in the components directly.
            loaders: [
                "exports-loader?module.exports.toString()",
                {
                    loader: "css-loader",
                    options: {
                        sourceMap: true,
                        importLoaders: 1
                    }
                },
                {
                    loader: "postcss-loader",
                    options: {
                        ident: "postcss",
                        config: {
                            path: path.join(basePath, "postcss.config.js")
                        }
                    }
                },
                {
                    loader: "sass-loader",
                    options: {
                        sourceMap: true,
                        precision: 8,
                        includePaths: []
                    }
                }
            ]
        }),

        // Needed for setting correct context.  This is magic that is required by Angular.
        new webpack.ContextReplacementPlugin(
            /angular(\\|\/)core(\\|\/)@angular/,
            path.join(basePath, "src")
        ),

        // Skip the emitting phase whenever there are errors while compiling.
        new webpack.NoEmitOnErrorsPlugin(),

        // Show progress.
        new webpack.ProgressPlugin(),

        new CopyWebpackPlugin([{ from: 'src/public' }]),
        new VcdPluginVersion(),
        new ZipPlugin({ filename: 'plugin.zip' }),
    ],

    // Configure minimizer to not interfere with DI as much as possible
    optimization: {
        minimizer: [
            new UglifyJSPlugin({
                uglifyOptions: {
                    keep_fnames: true
                }
            })
        ]
    }
}
