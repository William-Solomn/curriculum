const counter = (state=0,action)=>{
    switch (action.type){
        case "increament":
            return state+1
        case "decreamet":
            return state-1;
        default:
            return state;
    }

}
export default counter