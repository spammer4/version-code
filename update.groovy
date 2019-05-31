def String version = "2.1.4"

def sh(String command) {
    println command
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    return "$serr $sout".trim()
}

def getVersionFromPackageJson() {
    String awkCommand = 'awk -F\'"\' \'/"version": ".+"/{ print $4; exit; }\' package.json'
    return sh(awkCommand)
}

def updatePackageVersion(String version, Boolean isDevelop = true) {
    def String RELEASE_CLIENT_EXTENSION = "-rc"
    if (isDevelop) {
        if (version.contains(RELEASE_CLIENT_EXTENSION)) {
            println "Remove the release and commit"
            String newVersion = version.replace(RELEASE_CLIENT_EXTENSION,"")
            sh "yarn.cmd version --no-git-tag-version --new-version ${newVersion}"
            sh "git add ."
            sh "git commit -m 'Removed ${RELEASE_CLIENT_EXTENSION} from develop version ${version}'"
            sh "git push"
        }
        
        return
    } 

    // Release 
    if (!version.contains(RELEASE_CLIENT_EXTENSION)) {
        // Initial build of the release branch from develop
        println "Initial release cut, incrementing minor version number"
        sh "yarn.cmd version --no-git-tag-version --new-version minor"
    } else {
        // Hotfix, need to patch twice, the first removes the -release and the second 
        // increments the patch number 
        println "Hotfix applied, incrementing patch number"
        sh "yarn.cmd version --no-git-tag-version --new-version patch"
        sh "yarn.cmd version --no-git-tag-version --new-version patch"
    }

    // Get the updated version, append -release to it and tag it 
    String updatedVersion = getVersionFromPackageJson()
    sh "yarn.cmd version --new-version ${updatedVersion}${RELEASE_CLIENT_EXTENSION}"
}

String currentVersion = getVersionFromPackageJson()
updatePackageVersion(currentVersion, true)
/*def getUpdatedVersion(String current) {
        
}*/

/*def f = getVersionFromPackageJson()
s = f("hello", false)
print s*/