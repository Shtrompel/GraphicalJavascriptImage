package com.igalblech.school.graphicaljavascriptcompiler.utils.project;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import org.joda.time.Instant;

import java.io.Serializable;

import lombok.Getter;

/**
 * Contains data for each project.
 */
public class ProjectSettings implements Serializable {

    static final long serialVersionUID = 2134283670332539326L;

    private @Getter int width, height;
    private @Getter UserData userData;
    private @Getter RenderColorFormat format;
    private @Getter String code = "";
    private @Getter String title = "";
    private @Getter String description = "";
    private @Getter Instant dateCreated;
    private @Getter Instant lastUpdated;

    private @Getter int voteCount = 0;
    private @Getter int views = 0;
    private @Getter float ratings = 0.0f;

    public ProjectSettings( UserData userData, RenderColorFormat format, int width, int height) {
        this.userData = userData;
        this.format = format;
        this.width = width;
        this.height = height;
        dateCreated = Instant.now ();
        lastUpdated = Instant.now ();
    }

    public void setProjectInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void updateDate() {
        this.lastUpdated = Instant.now ();
    }

    public void addView() {
        this.views++;
    }

    public void addVote(float rating) {
        if (voteCount == 0) {
            this.ratings = rating;
        }
        else {
            this.ratings =  (this.ratings * voteCount + rating) / (voteCount + 1.0f);
        }
        voteCount++;
    }

    @NonNull
    @Override
    protected Object clone ( ) throws CloneNotSupportedException {
        ProjectSettings ret = (ProjectSettings) super.clone();
        ret.width = width;
        ret.height = height;
        ret.format = (RenderColorFormat) this.format.clone ();
        ret.code = this.code;
        ret.title = this.title;
        ret.description = this.description;
        ret.dateCreated = this.dateCreated;
        ret.lastUpdated = this.lastUpdated;
        ret.userData = userData;
        return ret;
    }

    public void setDates ( Instant dateCreated, Instant lastUpdated ) {
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
    }
}