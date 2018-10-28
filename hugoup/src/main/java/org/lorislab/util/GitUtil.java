package org.lorislab.util;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitUtil {

    public static String getVersion() throws Exception {
        Git git = Git.open(new File("."));
        Repository repo = git.getRepository();
        ObjectId lastCommitId = repo.resolve(Constants.HEAD);
        RevCommit revc = repo.parseCommit(lastCommitId);
        ObjectId commitId = revc.getId();
        String shortId = commitId.abbreviate(7).name();
        return shortId;
    }
}
