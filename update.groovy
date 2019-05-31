def String version = "2.1.4"

def sh(String command) {
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    return "$serr $sout".trim()
}

def updatePackageVersion(String version, Boolean isDevelop = true) {
    if (isDevelop) {
        if (version.contains("-release")) {
            println "Remove the release and commit"
            String newVersion = version.replace("-release","")
            sh "yarn.cmd version --no-git-tag-version --new-version ${newVersion}"
            sh "git add ."
            sh "git commit -m 'Removed -release from tag'"
            sh "git push"
        }
        
        return
    } 

    // Release 

    return
}

def getVersionFromPackageJson() {
    String awkCommand = 'awk -F\'"\' \'/"version": ".+"/{ print $4; exit; }\' package.json'
    return sh(awkCommand)
}

String currentVersion = getVersionFromPackageJson()
updatePackageVersion(currentVersion)
/*def getUpdatedVersion(String current) {
        
}*/

/*def f = getVersionFromPackageJson()
s = f("hello", false)
print s*/