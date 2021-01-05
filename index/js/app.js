window.onload = function(){
    
    $("section").on("mousemove", function(e){
        // let x = e.pageX;
        // let y = e.pageY;
        let {pageX:x, pageY:y}  = e;
        
        $(".obj11").css({"bottom": 20 + y / 30 + "px", "right": 20 + x / 30 + "px"});
        $(".obj12").css({"bottom": -40 - y / 30 + "px", "right": 130 - x / 30 + "px"});
        $(".obj13").css({"top": 180 + y / 30 + "px", "right": 60 + x / 30 + "px"});

        //두번째 섹션 
        $(".obj21").css({"right":-180-(x/30), "bottom":-480-(y/30)}); 
        $(".obj22").css({"right":130+(x/50), "bottom":-40-(y/50)}); 
        //세번째 섹션 
        $(".obj31").css({"right":280-(x/30), "bottom":30-(y/30)}); 
        $(".obj32").css({"right":110+(x/20), "bottom":-270+(y/20)}); 
        $(".obj33").css({"right":-70+(x/20), "bottom":-130+(y/20)}); 
        
        //네번째 섹션 
        $(".obj41").css({"right":20-(x/30), "bottom":-120-(y/30)}); 
        $(".obj42").css({"right":0+(x/30), "bottom":-180+(y/20)});
    });

    $("#menu > li").on("click", function(e){
        e.preventDefault(); //prevent => 예방하다, Default => 기본 => 이건 기본동작을 막는 코드이다.

        //!!!주의사항 this 제이쿼리에서 쓸 때는 함수를 화살표 함수를 쓰면 안된다.
        let idx = $(this).index(); //몇번째 애가 클릭된건지를 알려준다.

        let h = $("section").height();
        let scrollDistance = h * idx; // 몇번째 애인지를 높이에 곱해준다.
        
        $("html, body").stop().animate({"scrollTop": scrollDistance}, 1000);
    });
    
    $(window).on("scroll", function(e){
        const y = window.scrollY;

        $("#menu > li").removeClass("on");

        //판별을 시작해서 y값에 따라서 menu > li중에서 on이 붙어야할 녀석을 판별해주는 거
        const h = $("section").height();

        if( y >= 0 && y < h) {
            $("#menu > li").eq(0).addClass("on");
        }else if( y < 2 * h){
            $("#menu > li").eq(1).addClass("on");
        }else if( y < 3 * h){
            $("#menu > li").eq(2).addClass("on");
        }else if( y < 4 * h){
            $("#menu > li").eq(3).addClass("on");
        }
        //기능반 제외, 2명까지 비밀채팅으로 보내서
        // 정상작동시 선물 보내드림.
        // 명재, 전제, 조한슬, 정나영(구)
    });

    window.scrolling = false;

    $("section").on("wheel", function(e){
        e.preventDefault();
        if(scrolling) return;

        const d = e.originalEvent.deltaY;
        const idx = $(this).index();
        const h = $("section").height();
        
        if(d > 0 && idx !== 3){  //스크롤을 아래로 내리는거
            scrolling = true;
            $("html, body").stop().animate({"scrollTop": h * (idx + 1)}, 1000, function(){
                //여기가 애니메이션 종료후 실행
                console.log("스크롤 종료");
                scrolling = false;
            });
        }else if(d < 0 && idx !== 0) { //스크롤을 위로 올리는 거야
            scrolling = true;
            $("html, body").stop().animate({"scrollTop": h * (idx - 1)}, 1000, function(){
                //여기도 애니메이션이 종료되면 실행
                console.log("asd");
                scrolling = false;
            });
        }
    });

    //스크롤을 내리면서 애니메이션이 발생하는 동안에는 
    //다시 WHEEL이 안먹도록

    // document.querySelector("section").addEventListener("wheel", function(e){
    //     console.log(e);
    // });
}