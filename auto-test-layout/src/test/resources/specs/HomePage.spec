@objects
	header							header
	desktop-header					div.nu-header-main
		desktop-header-content		div.container
		desktop-logo-container		div[class='logo-box pull-left ga_header_logo']
	header-boundary					._2O0Ai
	header-horizontal				._O-PIH
	burgermenu-checkbox				#burgerMenu
	burgermenu-span					label[for='burgerMenu'] span._1MnGq
	burgermenu-container			._JG2uB
	logo-container					._15ofQ
	add-to-home-button-icon			label[for='AddToHomeButton'] div._14lMx span._aTjvl
	search-bar-container			div[class='_KORY2 _2Ryhe _J3eTN _1Ufmj _20qI_']
	search-input					input._3WJS7
	banner-container				div[class='_12Kxe _20qI_']
	banner-content					div[class='_EMu2C _Yn3i2 _3tv75 _2ewpB']
	banner-heading1					h1[class='_2JTvU _3aaLY _3fM6M _1Ua4U']
	banner-heading2					h2[class='_26xc2 _3aaLY _1Ua4U _2atJ8']
	plan-holiday-button				div[class='_EMu2C _Yn3i2 _3tv75 _2ewpB'] button._2ewpB
	plan-holiday-button-content		button._2ewpB div._2SBE5
	looking-for-section				div[class='_12Kxe _1Bs8D _1hklU _20qI_']
		parah						p[class='_3_4ml _3os3k _3fM6M']
		inner-container				div[class='_3Rm72 _3Dp61 _Yn3i2']
		first-section				div[class='_3Rm72 _3Dp61']
		second-section				div[class='_3Rm72 _3Dp61 _3os3k _1Ufmj']
		family-subsection			a[href='/Family-Places'] div[class='_2ScMM _20qI_ _QjSco']
		family-content-div			div[class='_20qI_ _QjSco _1Xt6s _13zNC']
		family-parah				div[class='_20qI_ _QjSco _1Xt6s _13zNC'] p[class='_EMu2C _2JTvU _1Ua4U _3GzLv _2ewpB']
		family-end-div				div[class='_20qI_ _QjSco _1Xt6s _13zNC'] div._ZOFZL
		adventure-subsection		a[href='/Adventure-Places'] div[class='_2ScMM _20qI_ _QjSco']
		adventure-content-div		div[class='_20qI_ _QjSco _2LNvS _13zNC']
		adventure-parah				div[class='_20qI_ _QjSco _2LNvS _13zNC'] p[class='_EMu2C _2JTvU _1Ua4U _3GzLv _2ewpB']
		adventure-end-div			div[class='_20qI_ _QjSco _2LNvS _13zNC'] div._ZOFZL
	explore-section					div[class='_3Rm72 _3Dp61 _2Ryhe _J3eTN _2Z9ow']
		explore-content				div[class='_2ScMM _QjSco']
		explore-india				div[class='_1Xt6s']
			content					div[class='_1Xt6s'] div[class='_3Rm72 _3Dp61 _Yn3i2 _1hklU _1WM8M destination-type-box']
		explore-international		div[class='_2LNvS']
			content					div[class='_2LNvS'] div[class='_3Rm72 _3Dp61 _Yn3i2 _1hklU _1WM8M destination-type-box']
	marketplace-section				div[class='_3Rm72 _3Dp61 _Yn3i2 _2Ryhe _J3eTN _2Z9ow']
		sub-section					div[class='_cMPG8 _3fM6M _2Ryhe _J3eTN _1hklU']
		heading3					h3._3_4ml
		end-section					div[class='_3Rm72 _3Dp61 _27qya']	
	
	
	
== Header ==

	@on mobile
		header-boundary:
			css background-color is "rgba(32, 163, 151, 1)"
			css padding is "12px 10px"
						
		header-horizontal:
			height 22px
			css padding-left is "10px"
			css padding-right is "10px"

		burgermenu-span:
			width 20px
			height 14px
			css display is "block"
			css border-top is "1px solid rgb(255, 255, 255)"
			css border-bottom is "1px solid rgb(255, 255, 255)"
			
		burgermenu-container:
			width 20px
			height 16px
			css float is "left"
			css padding-top is "2px"
			
			
		logo-container:
			width 140px
			css float is "left"
			css margin-left is "16px"
			right-of burgermenu-span ~ 16px
						
		add-to-home-button-icon:
			css border is "1px solid rgb(255, 255, 255)"
			css line-height is "18px"
			css display is "block"
			css border-radius is "2px"
			css color is "rgba(255, 255, 255, 1)"
			css text-align is "center"	
			right-of burgermenu-span ~ 285px
	
	@on desktop
		desktop-header:
			width 100% of screen/width
			css font-family is "Roboto, arial"
		
		desktop-header.desktop-header-content:
			css padding-left is "15px"
			css padding-right is "15px"
		
		desktop-header.desktop-logo-container:
			css float is "left"
			css font-size is "13px"
			inside desktop-header.desktop-header-content 15px left			
			
== Search Bar ==
	
	@on mobile
		search-bar-container:
			css background-color is "rgba(32, 163, 151, 1)"
			css padding-right is "8px"
			css padding-left is "8px"
			css padding-bottom is "8px"
			below header 0px
			
		search-input:
			height 48px
			css font-size is "14px"
			width 95% of search-bar-container/width
			css padding is "16px 50px 16px 16px"
			css background-color is "rgba(255, 255, 255, 1)"
		
== Banner ==
	@on mobile
		banner-container:
			css margin is "8px"
			below search-bar-container 8px
		
		banner-content:
			css text-align is "center"
			css z-index is "2"
			inside banner-container 30px bottom
		
		banner-heading1:
			text is "Design & Book Amazing Holiday Packages"
			css padding-bottom is "15px"
			css font-weight is "normal"
			css font-size is "24px"
			css line-height is "28px"
			css color is "rgba(255, 255, 255, 1)"
			css font-family is "Lato, sans-serif"
			
		banner-heading2:
			text is "with 650+ experts for 65+ Destinations"
			below banner-heading1 0px
			css padding-bottom is "24px"
			css font-weight is "normal"
			css font-size is "14px"
			css line-height is "20px"
			css color is "rgba(255, 255, 255, 1)"
			css font-family is "Lato, sans-serif"
			
		plan-holiday-button:
			below banner-heading2 0px
			width 100% of banner-content/width

		plan-holiday-button-content:		
			css display is "inline-block"
	    	css font-size is "14px"
	    	css line-height is "14px"
	    	css padding is "12px 25px"
	    	css background-color is "rgba(254, 82, 70, 1)"
	    	css color is "rgba(255, 255, 255, 1)"
	    	css font-weight is "bold"
	    	
== looking-for section ==
	
	@on mobile  
	   
	    looking-for-section:
	    	below banner-container 8px
	    	css margin is "8px"
	    	css padding is "8px"
	    	css position is "relative"
	    	css background-color is "rgba(255, 255, 255, 1)"
	    
	    looking-for-section.parah:
	    	text is "What are you looking for?"
	    	css font-size is "16px"
    		css line-height is "24px"
    		css color is "rgba(32, 163, 151, 1)"
    		css font-weight is "bold"
    		css text-align is "center"
    	
    	looking-for-section.inner-container:
    		css margin is "0px"
    		css text-align is "center"
    		inside looking-for-section ~55px top
    		
    	looking-for-section.first-section:
    		css margin is "0px"
    		css font-family is "Lato, sans-serif"
    		width 100% of looking-for-section.inner-container/width
	    	css font-size is "14px"
	    
	    looking-for-section.second-section:
	    	css margin is "0px"
	    	css padding-bottom is "8px"
	    	css padding-top is "8px"
	    	css font-family is "Lato, sans-serif"
    		width 100% of looking-for-section.inner-container/width
	    	css font-size is "14px"
	    	
	    looking-for-section.family-subsection:
	    	width 50% of looking-for-section.inner-container/width
	    	css padding is "0px"
	    	css position is "relative"
	    	css text-align is "center"
	    
	    looking-for-section.family-content-div:
	    	height 140px
	    	css overflow is "hidden"
	    	css background-color is "rgba(204, 204, 204, 1)"
	    	css text-align is "center"
	    	inside looking-for-section.family-subsection 4px right
	    
	    looking-for-section.adventure-subsection:
	    	width 50% of looking-for-section.inner-container/width
	    	css padding is "0px"
	    	css position is "relative"
	    	css text-align is "center"
	    
	    looking-for-section.adventure-content-div:
	    	height 140px
	    	css overflow is "hidden"
	    	css background-color is "rgba(204, 204, 204, 1)"
	    	css text-align is "center"
	    	css margin-left is "4px"
	    	inside looking-for-section.adventure-subsection 4px left
	    
	    looking-for-section.adventure-parah:
	   		css z-index is "3"
	   		css position is "absolute"
	   		width 100% of looking-for-section.adventure-content-div/width
	   		css font-size is "24px"
	   		css line-height is "28px"
	   		css color is "rgba(255, 255, 255, 1)"
	   		css left is "80px"
	   		text is "Adventure"

== Explore ==
	
	@on mobile
	
		explore-section:
			css margin-left is "0px"
			css margin-right is "0px"
			css margin-bottom is "8px"
			css padding-right is "8px"
			css padding-left is "8px"
			width 100% of looking-for-section/width
			
		explore-section.explore-content:
			width 47% of explore-section/width
			css float is "left"
		explore-section.explore-india:
			css margin-right is "4px"

			
			   		