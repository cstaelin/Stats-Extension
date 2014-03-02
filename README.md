# NetLogo stats Extension

* [Quickstart](#quickstart)
* [What is it?](#what-is-it)
* [Installation](#installation)
* [Examples](#examples)
* [Primitives](#primitives)
* [Building](#building)
* [Author](#author)
* [Feedback](#feedback-bugs-feature-requests)
* [Credits](#credits)
* [Terms of use](#terms-of-use)

## Quickstart

[Install the stats extension](#installation)

Include the extension in your NetLogo model (at the top):

    extensions [stats]

[back to top](#netlogo-stats-extension)

## What is it?

This package contains the NetLogo **stats extension**, which provides NetLogo with a tables for holding data gathered during runs, a number of statistical procedures to use on those data, and access to a number of useful distributions including the normal, students t, binomial, beta, gamma, and Chi squared distributions. Documentation of the features and use of the **stats extension** is found in the accompanying manual.

[back to top](#netlogo-stats-extension)

## Installation

First, [download the latest version of the extension](https://github.com/cstaelin/Stats-Extension/releases). Note that the latest version of this extension was compiled against NetLogo 5.0.5. If you are using a different version of NetLogo you might consider building your own jar file ([see building section below](#building)).

Unzip the archive, rename the extracted directory to **stats**, and move the **stats** directory to the **extensions** directory inside your NetLogo application folder. The NetLogo application will normally be in the Applications folder on the Mac, or under C:\Program Files (x86) on Windows.  Or you can place the stats directory in the same directory holding the NetLogo model in which you want to use this extension.

For more information on NetLogo extensions:
[http://ccl.northwestern.edu/netlogo/docs/extensions.html](http://ccl.northwestern.edu/netlogo/docs/extensions.html)

[back to top](#netlogo-stats-extension)

## Examples

See the **stats.nlogo** model for examples of usage.


[back to top](#netlogo-stats-extension)

## Primitives

Descriptions of all the stats primitives and their uses are contained in the accompanying manual.

[back to top](#netlogo-stats-extension)

## Building

The Makefile uses the NETLOGO environment variable to find the NetLogo installation. However, if NETLOGO has not been defined, the Makefile assumes that it is being run from the **extensions/stats** directory under the directory in which NetLogo has been installed. If compilation succeeds, `stats.jar` and `stats.jar.pack.gz` will be created.  See [Installation](#installation) for instructions on where to put the new `stats.jar` and `stats.jar.pack.gz` if they are not already there.

## Author

Charles Staelin<br>
Smith College<br>
Northampton, MA 01063

## Feedback? Bugs? Feature Requests?

Please visit the [github issue tracker](https://github.com/cstaelin/Stats-Extension/issues?state=open) to submit comments, bug reports, or feature requests.  I'm also more than willing to accept pull requests.

## Credits

Many thanks to the NetLogo developers and the NetLogo user community for answering my questions and suggesting  additional features.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo stats extension is in the public domain.  To the extent possible under law, Charles Staelin has waived all copyright and related or neighboring rights.

[back to top](#netlogo-stats-extension)
