package com.igalblech.school.graphicaljavascriptcompiler.utils.project;

import androidx.annotation.NonNull;

import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserData;

import java.io.Serializable;
import java.util.Date;

public class ProjectSettings implements Serializable {

    public int width = -1, height = -1;
    public UserData userData = null;
    public RenderColorFormat format = null;
    public String code = "";
    public String title = "";
    public String description = "";
    public Date dateCreated = null;
    public Date lastUpdated = null;

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


}