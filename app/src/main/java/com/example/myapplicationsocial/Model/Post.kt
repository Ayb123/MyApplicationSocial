package com.example.myapplicationsocial.Model

class Post {
    private var postId: String=""
    private var postimage:String=""
    private var publisher: String=""
    private var description:String=""

    constructor()


    constructor(postId: String, postimage: String, publisher: String, description: String) {
        this.postId = postId
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
    }



    fun getPostId():String{
        return postId
    }
    fun getPostimage():String{
        return postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getdescription():String{
        return description
    }

    fun setPostId(postId:String)
    {
        this.postId=postId
    }
    fun setPosimage(postimage: String)
    {
        this.postimage=postimage
    }
    fun setPublisher(publisher: String)
    {
        this.publisher=publisher
    }
    fun setdescription(description: String)
    {
        this.description=description
    }
}