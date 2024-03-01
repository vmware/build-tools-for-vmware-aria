import { foo, tail, setAlignment } from "./action1";

tail([1,2,3,4],[5,6,7,8]);


foo([1,32]); // works error
setAlignment("top-left");   // works!
