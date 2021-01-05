class Rectangle{
    constructor(height, width){
        this.height = height;
        this.width = width;
        this.n = "사각형";
    }

    get area(){
        return this.height * this.width;
    }
    get name(){
        return this.n;
    }
    set name(value){
        this.n = value;
    }
}