

/**
 * Gets a list of downstream jobs.
 *
 * Searches the Jenkins instance for all available jobs scoped to the specified folder. Filters out the current job, filters out those that are not
 * within the folder specified by JENKINS_FOLDER_NAME, and only collects those with the same name as this job. 
 * 
 * A folder in this context is one created in Jenkins to hold multiple pipelines. It does not refer to any filesystem
 * or source control system.
 *
 * The names are matched in order to run specific branches of a monorepo. For example, running the 'develop' branch
 * of this pipeline should only run the 'develop' branche of the downstream projects.
 */
def getFilteredJobs(String folder = ".*") {
    echo "Getting jobs scoped with ${folder}"
    def pattern = ~/${folder}/

    return Jenkins.instance.getAllItems(Job.class).findAll{
        (it.fullName != "${JOB_NAME}") &&                       //Don't get current job
        (it.fullName.split('/')[0] ==~ pattern) &&               //Only get those in folder
        (it.name == BRANCH_NAME)                                //Only get matching branches
    }.collect{
        def buildName = it.fullName.split('/')[1]
        def jobScriptPath = it.definition.scriptPath.split("/")[0]

        ["buildName" : buildName, "jobScriptPath" : jobScriptPath, "jobPath" : it.fullName]
    }
}