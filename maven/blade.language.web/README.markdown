# Language Web

The Language Web sample is a simple JSP portlet that conveys Liferay's
recommended approach to sharing language keys through OSGI services.

You must deploy this sample with the `blade.language` sample module. The
Language Web portlet sample uses language keys shared by the Language module.
When you place this sample portlet on a Liferay Portal page, you're presented
with the portlet's name followed by three language keys.

![Figure 1: The Language Web portlet displays three phrases, two of which are shared from a different module.](https://github.com/codyhoag/liferay-docs/blob/blade-sample-images/develop/tutorials/blade-images/language-web-portlet.png)

The first message is provided by the Language Web module. The second message is
from the Language module. The third message is provided by both modules; as you
can see, the Language Web's message is used, overriding the Language module's
identically named language key.

This sample shows what takes precedence when displaying language keys. The order
for this example goes

1.  Language Web module language keys
2.  Language module language keys
3.  Liferay Portal language keys

You can visit the
[blade.language README](https://github.com/liferay/liferay-blade-samples/tree/master/gradle/blade.language/README.markdown)
for more information on how language keys are used from modules and shared
between modules.