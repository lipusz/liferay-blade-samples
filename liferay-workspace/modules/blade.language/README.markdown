# Language

The Language sample conveys Liferay's recommended approach to sharing
language keys through OSGI services. This particular sample provides a resource
module which only holds language keys.

You should deploy this sample with the `blade.language.web` sample module. This
sample shares language keys with the Language Web module. How does this work?

By default, the `ResourceBundleLoaderAnalyzerPlugin` expands modules with
`/content/Language.properties` files to add provided capabilities:

- `bundle.symbolic.name`
- `resource.bundle.base.name`

Then the deployed `LanguageExtender` scans modules with those capabilities to
automatically register an associated `ResourceBundleLoader`.

You can leverage this functionality to use keys from common language modules by
republishing an aggregate `ResourceBundleLoader`. This can be done two ways:

1. Via Components

    You can get a reference to the registered service in your components as
    detailed in the
		[Overriding a Module's Language Keys](https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/overriding-a-modules-language-keys)
		tutorial. The main disadvantage of this approach is that it forces you to
		provide a specific implementation of the `ResourceBundleLoader`, making it
		harder to modularize in the future.

2. Via Provide Capability

    The same `LanguageExtender` that registers the services supports an extended
    syntax that lets you register an aggregate of a collection of bundles:

        -liferay-aggregate-resource-bundles: \
            blade.language

    This approach has the advantage of easier extensibility. Only the common
    language modules need to be built and redeployed when keys change for all
    modules using them to automatically pick up the changes while staying clear
    of implementation details.

Visit the
[blade.language.web README](https://github.com/liferay/liferay-blade-samples/tree/master/gradle/blade.language.web/README.markdown)
to see how the Language sample module shares its language keys with a JSP
portlet.