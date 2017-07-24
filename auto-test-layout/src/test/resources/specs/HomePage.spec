@objects
	ds-header-main .nu-header-main
		ds-header-container	.container
	ds-logo-container	xpath	//div[@class='logo-box pull-left ga_header_logo']
		logo-image	img
	image-logo	xpath //img[@alt='TravelTriangle']



== Header ==

	@on desktop
		ds-header-main:
			width 100 % of screen/width
			height ~ 50 px
		ds-header-main.ds-header-container:
			width 100% of screen/width
			inside ds-header-main ~ 900 px right
			css padding-right is "15px"
    		css padding-left is "15px"
    		css margin-right is "90.5px"
    		css margin-left is "90.5px"
		ds-logo-container:
			inside ds-header-main.ds-header-container ~790 px right
			css float is "left"
			width ~ 220 px
		ds-logo-container.logo-image:
			width 100 % of ds-logo-container/width
			inside ds-logo-container	
			


	@on mobile
		image-logo:
			css font-size is "1px"
			css line-height is "18px"
					
		