function version_compare(v1, v2, operator) {
    // http://kevin.vanzonneveld.net
    // +      original by: Philippe Jausions (http://pear.php.net/user/jausions)
    // +      original by: Aidan Lister (http://aidanlister.com/)
    // + reimplemented by: Kankrelune (http://www.webfaktory.info/)
    // +      improved by: Brett Zamir (http://brett-zamir.me)
    // +      improved by: Scott Baker
    // +      improved by: Theriault
    // *        example 1: version_compare('8.2.5rc', '8.2.5a');
    // *        returns 1: 1
    // *        example 2: version_compare('8.2.50', '8.2.52', '<');
    // *        returns 2: true
    // *        example 3: version_compare('5.3.0-dev', '5.3.0');
    // *        returns 3: -1
    // *        example 4: version_compare('4.1.0.52','4.01.0.51');
    // *        returns 4: 1
    // BEGIN REDUNDANT
    this.php_js = this.php_js || {};
    this.php_js.ENV = this.php_js.ENV || {};
    // END REDUNDANT
    // Important: compare must be initialized at 0.
    var i = 0, x = 0, compare = 0, // vm maps textual PHP versions to negatives so they're less than 0.
    // PHP currently defines these as CASE-SENSITIVE. It is important to
    // leave these as negatives so that they can come before numerical versions
    // and as if no letters were there to begin with.
    // (1alpha is < 1 and < 1.1 but > 1dev1)
    // If a non-numerical value can't be mapped to this table, it receives
    // -7 as its value.
        vm = {
            'dev': -6,
            'INTEGRATION': 6,
            'alpha': -5,
            'a': -5,
            'TEST': -5,
            'beta': -4,
            'b': -4,
            'RELEASE': -4,
            'RC': -3,
            'rc': -3,
            '#': -2,
            'p': 1,
            'pl': 1
        }, // This function will be called to prepare each version argument.
    // It replaces every _, -, and + with a dot.
    // It surrounds any nonsequence of numbers/dots with dots.
    // It replaces sequences of dots with a single dot.
    //    version_compare('4..0', '4.0') == 0
    // Important: A string of 0 length needs to be converted into a value
    // even less than an unexisting value in vm (-7), hence [-8].
    // It's also important to not strip spaces because of this.
    //   version_compare('', ' ') == 1
        prepVersion = function (v) {
            v = ('' + v).replace(/[_\-+]/g, '.');
            v = v.replace(/([^.\d]+)/g, '.$1.').replace(/\.{2,}/g, '.');
            return (!v.length ? [-8] : v.split('.'));
        }, // This converts a version component to a number.
    // Empty component becomes 0.
    // Non-numerical component becomes a negative number.
    // Numerical component becomes itself as an integer.
        numVersion = function (v) {
            return !v ? 0 : (isNaN(v) ? vm[v] || -7 : parseInt(v, 10));
        };
    v1 = prepVersion(v1);
    v2 = prepVersion(v2);
    x = Math.max(v1.length, v2.length);
    for (i = 0; i < x; i++) {
        if (v1[i] == v2[i]) {
            continue;
        }
        v1[i] = numVersion(v1[i]);
        v2[i] = numVersion(v2[i]);
        if (v1[i] < v2[i]) {
            compare = -1;
            break;
        } else if (v1[i] > v2[i]) {
            compare = 1;
            break;
        }
    }
    if (!operator) {
        return compare;
    }

    // Important: operator is CASE-SENSITIVE.
    // "No operator" seems to be treated as "<."
    // Any other values seem to make the function return null.
    switch (operator) {
        case '>':
        case 'gt':
            return (compare > 0);
        case '>=':
        case 'ge':
            return (compare >= 0);
        case '<=':
        case 'le':
            return (compare <= 0);
        case '==':
        case '=':
        case 'eq':
            return (compare === 0);
        case '<>':
        case '!=':
        case 'ne':
            return (compare !== 0);
        case '':
        case '<':
        case 'lt':
            return (compare < 0);
        default:
            return null;
    }
}

var map_version_function = function () {
    var key = {
        org: this.org,
        name: this.name,
        type: this.type,
        status: this.status
    };
    var value = {
        version: this.version,
        isForce: this.isForce
    };
    emit(key, value);
}

var reduce_latestversion_function = function (key, values) {

    var maxVersionDoc;
    var curMaxVersionVal = "";
    var BreakException = {};
    try {
        values.forEach(
            function (curObj) {

                //-- Get current version from values element
                var curVersionVal = curObj.version;

                //-- Case of isForce version
                if (curObj.isForce) {
                    maxVersionDoc = curObj;
                    throw BreakException;
                }

                if (version_compare(curVersionVal, curMaxVersionVal, ">")) {
                    curMaxVersionVal = curVersionVal;
                    maxVersionDoc = curObj;
                }
            }
        );

        return  maxVersionDoc;

    } catch (e) {
        if (e === BreakException) {
            return maxVersionDoc;
        }

        throw e;
    }
}

var reduce_deprecatedversion_function = function (key, values) {

    var maxVersionDoc = reduce_latestversion_function(key, values);
    var maxVersionVal = maxVersionDoc.version

    var versionTab = [];
    values.forEach(
        function (value) {
            var curVersionVal = value.version;
            if (version_compare(curVersionVal, maxVersionVal, "<")) {
                versionTab.push(curVersionVal);
            }
        }
    );
    return {
        version: versionTab.join(" ")
    };
}

function finalize_function(key, value) {
    return value.version;
}
