<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math"
>
  <xsl:output indent="yes" />
   
  <xsl:template match="testSuiteResults">
    <body>
	<head>
      <title>Test Report</title>
	  <script>
                                                 function doMenu(item,obj) {
                                                                            var table = document.getElementById(item);
																			var object = document.getElementById(obj);
                                                                            if (table.style.display=='none') 
                                                                              {
                                                                                 table.style.display='block'; 
                                                                                 object.innerHTML= '[-]';
                                                                               }
                                                                             else {
                                                                                  table.style.display='none';
                                                                                  object.innerHTML= '[+]';
                                                                                  }
                                                                             }
function showPopup(error_message){
alert(error_message);
}																				

                                            </script>
      <style type="text/css">
	  .black_overlay{
        display: none;
        position: absolute;
        top: 0%;
        left: 0%;
        width: 100%;
        height: 100%;
        background-color: black;
        z-index:1001;
        -moz-opacity: 0.8;
        opacity:.80;
        filter: alpha(opacity=80);
    }
	#errorDetail{
	font-style: italic;
	color: red;
	}
	#testStep{
	font-weight: bolder;
	color: darkblue;
	padding-bottom: 2%;
	}
	#close{
	float: right;
	z-index: 5000;
	}
    .white_content {
        display: none;
        position: absolute;
        top: 25%;
        left: 25%;
        width: 50%;
        height: 50%;
        padding: 16px;
        border: 3px solid orange;
        background-color: whitesmoke;
        z-index:1002;
        overflow: auto;
		font-family: monospace;
    }	  
	  .containerDiv { 
 			 border: 1px solid orange; 
 			width: 100%; 
 			} 
 			 
 			.rowDivHeader { 
 			border: 1px solid orange; 
 			background-color: orange; 
 			color: black; 
 			font-weight: inherit; 
 			} 
 			 
 			.rowDiv { 
 			 
 			background-color: whitesmoke; 
 			} 
 			 
 			.cellDivHeader { 
 			border-right: 1px solid white; 
 			display: table-cell; 
 			width: 9%; 
 			padding: 1px; 
 			text-align: center; 
 			} 
 			 
 			.cellDiv { 
 			border-right: 1px solid white; 
 			display: table-cell; 
 			width: 9%; 
 			padding-right: 4px; 
 			text-align: center; 
 			border-bottom: none; 
 			} 
 			 
 			.lastCell { 
 			  border-right: none; 
 			} 
          #header{
		font-weight: bold;
		background-color: oldlace;
		border: 1px solid;
}
      
#TB_TestSuite {}
table {
    width: 100%;
}
#td_testSuite { 
 			background-color: rgb(64, 119, 64); 
 			background-color: rgb(184, 78, 78); 
 			z-index: -1; 
 			position: inherit; 
 			} 
 			#execution_Details{ 
 			display: block; 
 			height: 140px; 
 			padding-top: 4%; 
 			width: 50%; 
 			}

#test_Header {
    display: block;
    text-align: center;
    font-size: 32px;
    font-family: "Times New Roman", Times, serif;
}
#errorLink{
  text-decoration: inherit;
  color: red;
  font-variant: small-caps;
}
body {
    font-family: "Times New Roman", Times, serif;
}
			.headerRow{ 
 			background-color: blanchedalmond; 
 			font-weight: bold; 
 			color: black; 
 			} 
 			.contentRow{ 
 			background-color: antiquewhite; 
 			}
      </style>
   </head>
      
	  <div id="test_Header">  
 				<img src="http://static.jabong.com/images/jlite/logo-new.png" alt="Jabong.com" style="float: left;"></img> 
 			    <span>Test Execution Summary Report </span> 
 			  </div> 
 	 <xsl:for-each select="testSuite">
        <ul>
		
          <xsl:apply-templates select="." />
        </ul>
      </xsl:for-each>
    </body>
  </xsl:template>

  <xsl:template match="testSuite">
    <xsl:variable name="testSuite_Count" select="count(preceding-sibling::testSuite)"/>
	 <table id = "TB_TestSuite">
		<tr id = "header">
		<td style="width: 1%;"> </td>
		<td style="width: 33%;">TestSuiteName</td>
		<td style="width: 7%;">Start-Time</td>
		<td style="width: 10%;">Status</td>
		<td style="width: 33%;">TotalExecutionTime(msec)</td>
		</tr>
		
		<tr style="height: 55px;">
		<td id = "expander" style="width: 2%;"> <a id="{concat('hs_',$testSuite_Count)}"  href='javascript:doMenu("{concat("containor_",$testSuite_Count)}","{concat("hs_",$testSuite_Count)}")'>[+]</a> </td>

		<xsl:choose>
		<xsl:when test="status = 'FINISHED'"> 
		<td id = "td_testSuite" style="background-color: rgb(52, 205, 52);"><xsl:value-of select="testSuiteName"/> </td>
		<td> <xsl:value-of select="startTime" /> </td>
		<td style="color: rgb(21, 131, 21);"> <xsl:value-of select="status" /></td>
		</xsl:when>
		<xsl:otherwise>
		<td id = "td_testSuite" style="background-color: rgb(194, 97, 105);">
		<xsl:value-of select="testSuiteName" /> </td>
		<td> <xsl:value-of select="startTime" /> </td>
		<td style="color: rgb(194, 97, 105);"> <xsl:value-of select="status" />
		</td>
		</xsl:otherwise>
		
		</xsl:choose>
		<td> <xsl:value-of select="timeTaken" /> </td>
		</tr>
		</table>	  
	  
      <xsl:if test="testSuite">
        <ul>
          <xsl:apply-templates select="testSuite" />
        </ul>
      </xsl:if>
	  <div id="{concat('containor_',$testSuite_Count)}" style="display: none;">
	  <xsl:for-each select="testRunnerResults/testCase">
	  <xsl:call-template name="TC">
	  <xsl:with-param name="TestCasePosition" select="math:random()"/>
	  <xsl:with-param name="TestSuitePosition" select="$testSuite_Count"/>
	  </xsl:call-template>
	  </xsl:for-each>
	  </div>
    
  </xsl:template>
  <xsl:template match="testRunnerResults/testCase" name="TC">
  <xsl:param name="TestSuitePosition"/>
  <xsl:param name="TestCasePosition"/>
  	<table id = "{concat('TB_TestCase',$TestSuitePosition)}">
		<tr id = "header">
		<td style="width: 2%;" id = "testcase_Expander"><a id="{concat('ns_',$TestCasePosition)}" href='javascript:doMenu("{concat("stepContainer_",$TestCasePosition)}","{concat("ns_",$TestCasePosition)}")'>[+]</a></td>
		<td style="width: 33%;"> TestCaseName</td>
		<td style="width: 7%;"> Start-Time </td>
		<td style="width: 10%;"> Status </td>
		<xsl:choose>
		<xsl:when test="status = 'FAILED'">
			<td style="width: 33%;">Error Details</td>
		</xsl:when>
		</xsl:choose>
		<td style="width: 33%;"> ExectionTime(msec) </td>
		</tr>
		<tr> 
		<td> </td>
		<td> <xsl:value-of select="testCaseName"/> </td>
		<td> <xsl:value-of select="startTime"/> </td>
		<xsl:choose>
		<xsl:when test="status = 'FAILED'">		
		
		<td style="color: rgb(255, 0, 31);"> <xsl:value-of select="status" /> </td>
		
		<td> 
		<a id="errorLink" href = "javascript:void(0)" onclick = "document.getElementById('{concat('light_',$TestCasePosition)}').style.display='block';document.getElementById('{concat('fade_',$TestCasePosition)}').style.display='block'" style="text-decoration: inherit;">Click To View Error Details</a> 
		</td>
			
			<xsl:for-each select="failedTestSteps/error">
				<div id="{concat('light_',$TestCasePosition)}" class="white_content">
				<button id="close" onclick = "document.getElementById('{concat('light_',$TestCasePosition)}').style.display='none';document.getElementById('{concat('fade_',$TestCasePosition)}').style.display='none'">X</button>
				<div id="testStep"><span>TestStepName : </span><xsl:value-of select="testStepName"/></div> 
				<div id="errorDetail"><span>Error Details : </span><span id="errorMessage"><xsl:value-of select="detail"/></span></div>
			 
			</div>
			<div id="{concat('fade_',$TestCasePosition)}" class="black_overlay"></div>
			</xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
		<td> <xsl:value-of select="status" /> </td>
		</xsl:otherwise>
		
		</xsl:choose>
		<td> <xsl:value-of select="timeTaken"/> </td>
		</tr>
	</table>
	<div id = "{concat('stepContainer_',$TestCasePosition)}" style="display: none;">
	<table>
	<tr id = "header">
	<td> </td>
	<td> TestStepExecuted </td>
	<td> Started </td>
	<td> Status </td>
	<td> Time Taken(msec) </td>
	<td> Remark </td>
	</tr>
	<xsl:for-each select="testStepResults/result">
	<xsl:choose>
	 <xsl:when test="status = 'FAILED'">
	<tr style="color: rgb(255, 0, 31);">
	<td> </td>
	<td><xsl:value-of select="name"/></td>
	<td><xsl:value-of select="started"/></td>
	<td><xsl:value-of select="status"/></td>
	<td><xsl:value-of select="timeTaken"/></td>	
	<td><xsl:value-of select="message"/></td>
	
	</tr>
	 </xsl:when>
	 <xsl:otherwise>
	 <tr style="color: rgb(16, 129, 16);">
	<td> </td>
	<td><xsl:value-of select="name"/></td>
	<td><xsl:value-of select="started"/></td>
	<td><xsl:value-of select="status"/></td>
	<td><xsl:value-of select="timeTaken"/></td>
	<td><xsl:value-of select="message"/></td>
	</tr>
	 </xsl:otherwise>
	</xsl:choose>
	</xsl:for-each>
	</table>
	</div>
  </xsl:template>
</xsl:stylesheet>