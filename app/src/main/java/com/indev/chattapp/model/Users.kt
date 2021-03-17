package com.indev.chattapp.model

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var website: String = ""

    constructor()

    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    //parameter for UID
    fun getUID(): String? {
        return uid
    }
    fun setUID(uid: String) {
        this.uid = uid
    }

    //parameter for username
    fun getUsername(): String? {
        return username
    }
    fun setUsername(username: String) {
        this.username = username
    }

    //parameter for profile
    fun getProfile(): String? {
        return profile
    }
    fun setProfile(profile: String) {
        this.profile = profile
    }

    //parameter for cover
    fun getCover(): String? {
        return cover
    }
    fun setCover(cover: String) {
        this.cover = cover
    }

    //parameter for status
    fun getStatus(): String? {
        return status
    }
    fun setStatus(status: String) {
        this.status = status
    }

    //parameter for search
    fun getSearch(): String? {
        return search
    }
    fun setSearch(search: String) {
        this.search = search
    }

    //parameter for facebook
    fun getFacebook(): String? {
        return facebook
    }
    fun setFacebook(facebook: String) {
        this.facebook = facebook
    }

    //parameter for facebook
    fun getInstagram(): String? {
        return instagram
    }
    fun setInstagram(instagram: String) {
        this.instagram = instagram
    }

    //parameter for facebook
    fun getWebsite(): String? {
        return website
    }
    fun setWebsite(website: String) {
        this.website = website
    }

}