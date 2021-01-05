const { rejects } = require("assert");
const { resolve } = require("path");

function getCovidData(){
    //여기서 뭔가 시간이 걸리는 작업이 이뤄지는거야
    return new Promise( (resolve, rejects) =>{
        setTimeout(()=>{
            resolve( "Hello world");
        },3000);
    });
}

getCovidData().then(x =>{
    console.log(x);
})