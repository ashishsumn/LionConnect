import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
// import { user } from 'firebase-functions/lib/providers/auth';
// import { user } from 'firebase-functions/lib/providers/auth';
// import field from "./filter";
// import { user } from 'firebase-functions/lib/providers/auth';
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

admin.initializeApp();
const db = admin.firestore();
interface user{
    firstName : string,
    lastName : string,
    emailId : string,
    posts : Map<String, Object>,
    follows : Map<String, Object>,
    followedBy : Map<String, Object>,
    feed : Map<String, Object>
}

export const createUserAddFeed = functions.firestore.document("users/{userId}").onCreate(async (snap, context) =>{
    const userid = context.params.userId;
    // const postMap : Map<String, Object> = new Map();
    console.log("New user ",userid)
    return db.collection("feed").doc(userid).set({docField : 0}, {merge :true}).then((val)=>{
      return null;
    }).catch((error)=>{
      console.log(error);
      return error;
    })
});


// Populate feed chained query
interface dataPostJson{
  [key : string] : string;
}
export const getPostInfo = functions.https.onCall( async (dataIn, context) =>{
    const userId = dataIn.user;
    let prevTs = dataIn.prevTs;
    let getFeedPostIds;
    console.log(dataIn);
    // const userId = "testUser"
    
    try {
      const promises : Promise<FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>>[] = []
      if( prevTs === undefined){
        getFeedPostIds = await db.doc("feed/" + userId)
        .collection("postTsMap").orderBy("timestamp","desc").limit(10).get();
        prevTs = -1;
      }else{
        console.log("StartAt ", prevTs + 10);
        getFeedPostIds = await db.doc("feed/" + userId)
        .collection("postTsMap").orderBy("timestamp","desc").limit(prevTs + 10).get();
      }

      const feedPosts = getFeedPostIds.docs.map((dataItem : FirebaseFirestore.QueryDocumentSnapshot)=>{

        const p : Promise<FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>> 
                      = db.collection("posts").doc(dataItem.id).get();
        promises.push(p)
        return dataItem.id
      });
      console.log(feedPosts);
      const data = await Promise.all(promises).then( docs =>{
        return Object.entries(docs);
      })

      const payload : Object[] = [];
      let counter = 0;
      data.forEach((value : [string, FirebaseFirestore.DocumentSnapshot])=>{
        let a : dataPostJson = {};
        counter = counter + 1;
        if(counter > prevTs){
          a["photos"] = value[1].get("photos");
          a["user"] = value[1].get("user");
          a["timestamp"] =  value[1].get("timestamp").toDate();
          // a["timestampObj"] = value[1].get("timestamp");
          if(value[1].get("description") === null){
            a["description"] =  "";
          }else{
            a["description"] = value[1].get("description");
          }
          a["userId"] = value[1].get("userId");
          // a["longTimestamp"] = value[1].get("timestamp")["_seconds"];
          // a["longTimestamp"] = value[1].get("timestamp");
          payload.push(a);
        }
      })
      const jsonPayload = { data : payload};

      console.log("Sent ",JSON.stringify(jsonPayload));
      // response.send(JSON.stringify(jsonPayload));
      return JSON.stringify(jsonPayload);
    } catch (error) {
      console.log(error)
      return JSON.stringify({error : {}});
    }

});

export const getMyFeed = functions.https
    .onCall(async (dataIn, context)=>{
      const userId = dataIn.user;
      const uName = dataIn.userName;
      let prevTs = dataIn.prevTs;
      let getFeedPostIds;
      console.log(dataIn);
      // const userId = "testUser"
      try {
        const promises : Promise<FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>>[] = []
        if( prevTs === undefined){
          getFeedPostIds = await db.doc("feed/" + userId)
          .collection("postTsMap").orderBy("timestamp","desc").get();
          prevTs = -1;
        }else{
          console.log("StartAt ", prevTs);
          getFeedPostIds = await db.doc("feed/" + userId)
          .collection("postTsMap").orderBy("timestamp","desc").limit(prevTs + 10).get();
        }
  
        const feedPosts = getFeedPostIds.docs.map((dataItem : FirebaseFirestore.QueryDocumentSnapshot)=>{
  
          const p : Promise<FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>> 
                        = db.collection("posts").doc(dataItem.id).get();
          promises.push(p)
          return dataItem.id
        });
        console.log(feedPosts);
        const data = await Promise.all(promises).then( docs =>{
          return Object.entries(docs);
        })
  
        const payload : Object[] = [];
        let counter = 0;
        data.forEach((value : [string, FirebaseFirestore.DocumentSnapshot])=>{
          counter = counter + 1;
          if(value[1].get("user") === uName && counter > prevTs){
              const a : dataPostJson = {};
              a["photos"] = value[1].get("photos");
              a["user"] = value[1].get("user");
              a["timestamp"] =  value[1].get("timestamp").toDate();
              // a["timestampObj"] = value[1].get("timestamp");
              if(value[1].get("description") === null){
                a["description"] =  "";
              }else{
                a["description"] = value[1].get("description");
              }
              a["userId"] = value[1].get("userId");
              a["longTimestamp"] = value[1].get("timestamp");
              payload.push(a);
          }
        })
        const jsonPayload = { data : payload};
  
        console.log("Sent for myPosts ",JSON.stringify(jsonPayload));
        // response.send(JSON.stringify(jsonPayload));
        return JSON.stringify(jsonPayload);
      } catch (error) {
        console.log(error)
        return JSON.stringify({error : {}});
      }
    });



// When a user posts add the post to their follower's feed
export const updateOtherFeed = functions.firestore
    .document("/users/{userId}").onWrite( async (change, context)=>{
      const afterData : user = <user>change.after.data();
      const beforeData : user = <user>change.before.data();
      let newPostKey = "";
      const promises = [];
      const currUser = context.params.userId;
      let tempString : string[] = [];
      const path = db.collection("/feed");
      const postMap: Map<string, Object> = new Map(Object.entries(afterData["posts"]));
      let postBeforeMap : Map<string, Object>;

      console.log(afterData);
      console.log(beforeData);

      if(beforeData["posts"] === undefined){
        postBeforeMap = new Map();
      }else{
        postBeforeMap = new Map(Object.entries(beforeData["posts"]));
      }
      const friendMap: Map<string, Object> = new Map(Object.entries(afterData["followedBy"]));

      postMap.forEach((value: Object, key: string) => {
        if(postBeforeMap.get(key) === undefined){
          newPostKey = key;
        }
      });

      friendMap.forEach((value: Object, friendId: string) => {

        // promises.push(path.doc(friendId).set( {postTsMap : {[`${newPostKey}`] : postMap.get(newPostKey) }}, {merge : true}));
        promises.push(path.doc(friendId).collection("postTsMap").doc(newPostKey)
                          .set( {timestamp : postMap.get(newPostKey) }, {merge : true}));
        tempString.push(friendId);
      });
      promises.push(path.doc(currUser).collection("postTsMap").doc(newPostKey)
                        .set({ timestamp : postMap.get(newPostKey)}, {merge:true}));
      console.log("Validating promises for ", tempString)
      Promise.all(promises).then((val) =>{
        return "Done";
      })
      .catch(error => {
        console.log("Error keeping the promise with ",error);
        return "Error" + error;
      })
});

export const tester = functions.https.onRequest(async (request,response)=>{
    return db.doc("users/testUser").get().then(snapshot =>{
        const userData : user = <user>snapshot?.data();
        response.send(userData);
        return;
    }).catch(error=>{
        console.log("Tester Error",error);
        response.send("Error ");
        return;
    });
});

// Add all the posts posted by user to the feed of new follower
export const addNewFriend = functions.firestore
  .document("users/{userId}").onUpdate(async (change, context) =>{
    const beforeData = <user>change.before.data();
    const afterData = <user> change.after.data();
    // const currUser = context.params.userId;
    let beforefriendMap: Map<string, Object>;
    let newFriendKey = "";
    const promises : Promise<FirebaseFirestore.WriteResult>[]  = [];
    const tempString : string[]= [];
    const path = db.collection("/feed");
    const afterfriendMap : Map<string, Object> = new Map(Object.entries(afterData["followedBy"]));
    const userPostMap : Map<string, Object> = new Map(Object.entries(afterData["posts"]));
    if(beforeData["followedBy"] === undefined){
      beforefriendMap = new Map();
    }else{
      beforefriendMap = new Map(Object.entries(beforeData["followedBy"]));
    }
    afterfriendMap.forEach((value: Object, key: string) => {
      if(beforefriendMap.get(key) === undefined){
        newFriendKey = key;
      }
    });

    if(newFriendKey === undefined){
      return null;
    }
    console.log("New follower is ",newFriendKey);
    userPostMap.forEach((value: Object, postId: string) => {

      // promises.push(path.doc(friendId).set( {postTsMap : {[`${newPostKey}`] : postMap.get(newPostKey) }}, {merge : true}));
      promises.push(path.doc(newFriendKey).collection("postTsMap").doc(postId)
                        .set( {timestamp : value }, {merge : true}));
      tempString.push(postId);
    });

    console.log("Validating promises for ", tempString);
    Promise.all(promises).then((val) =>{
      return "Done";
    })
    .catch(error => {
      console.log("Error keeping the promise with ",error);
      return "Error" + error;
    })
    return null;
});






