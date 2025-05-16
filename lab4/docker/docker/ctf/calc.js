/*
 * @Author: amamiya-yuuko-1225 1913250675@qq.com
 * @Date: 2025-05-16 15:21:55
 * @LastEditors: amamiya-yuuko-1225 1913250675@qq.com
 * @Description: 
 */
const vm = require("vm");

function main(line) {
    // 1. Input sanitization.
    const safePattern = /^[0-9+\-*/<>,()[\] .]*\b(Math\.min|Math\.max)?\b[0-9+\-*/<>,()[\] .]*$/;
    if (!safePattern.test(line))
        return "Invalid Input"

    // 2. Run script in a sandbox
    const env = {
        Math, // Global var that can be accessed
    };
    const context = vm.createContext(env);

    const options = {
        timeout: 1000,
    };

    const src = `"use strict";\n${line}`;

    try {
        return vm.createScript(src).runInNewContext(context, options);
    } catch (e) {
        return `Error: ${e.message}`;
    }

}

process.on("uncaughtException", function (err) {
    console.log("...");
});

module.exports = main;
