begin_unit
begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|AbstractClusteringTestCase
specifier|public
specifier|abstract
class|class
name|AbstractClusteringTestCase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|numberOfDocs
specifier|protected
specifier|static
name|int
name|numberOfDocs
init|=
literal|0
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"solr-clustering"
argument_list|)
expr_stmt|;
name|numberOfDocs
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|String
index|[]
name|doc
range|:
name|DOCUMENTS
control|)
block|{
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numberOfDocs
argument_list|)
argument_list|,
literal|"url"
argument_list|,
name|doc
index|[
literal|0
index|]
argument_list|,
literal|"title"
argument_list|,
name|doc
index|[
literal|1
index|]
argument_list|,
literal|"snippet"
argument_list|,
name|doc
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|numberOfDocs
operator|++
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|DOCUMENTS
specifier|final
specifier|static
name|String
index|[]
index|[]
name|DOCUMENTS
init|=
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"http://en.wikipedia.org/wiki/Data_mining"
block|,
literal|"Data Mining - Wikipedia"
block|,
literal|"Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns."
block|}
block|,
block|{
literal|"http://en.wikipedia.org/wiki/Datamining"
block|,
literal|"Data mining - Wikipedia, the free encyclopedia"
block|,
literal|"Data mining is the entire process of applying computer-based methodology, ... Moreover, some data-mining systems such as neural networks are inherently geared ..."
block|}
block|,
block|{
literal|"http://www.statsoft.com/textbook/stdatmin.html"
block|,
literal|"Electronic Statistics Textbook: Data Mining Techniques"
block|,
literal|"Outlines the crucial concepts in data mining, defines the data warehousing process, and offers examples of computational and graphical exploratory data analysis techniques."
block|}
block|,
block|{
literal|"http://www.thearling.com/text/dmwhite/dmwhite.htm"
block|,
literal|"An Introduction to Data Mining"
block|,
literal|"Data mining, the extraction of hidden predictive information from large ... Data mining tools predict future trends and behaviors, allowing businesses to ..."
block|}
block|,
block|{
literal|"http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm"
block|,
literal|"Data Mining: What is Data Mining?"
block|,
literal|"Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works."
block|}
block|,
block|{
literal|"http://www.spss.com/datamine"
block|,
literal|"Data Mining Software, Data Mining Applications and Data Mining Solutions"
block|,
literal|"The patterns uncovered using data mining help organizations make better and ... data mining customer ... Data mining applications, on the other hand, embed ..."
block|}
block|,
block|{
literal|"http://www.kdnuggets.com/"
block|,
literal|"KD Nuggets"
block|,
literal|"Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings."
block|}
block|,
block|{
literal|"http://www.answers.com/topic/data-mining"
block|,
literal|"data mining: Definition from Answers.com"
block|,
literal|"data mining n. The automatic extraction of useful, often previously unknown information from large databases or data ... Data Mining For Investing ..."
block|}
block|,
block|{
literal|"http://www.statsoft.com/products/dataminer.htm"
block|,
literal|"STATISTICA Data Mining and Predictive Modeling Solutions"
block|,
literal|"GRC site-wide menuing system research and development. ... Contact a Data Mining Solutions Consultant. News and Success Stories. Events ..."
block|}
block|,
block|{
literal|"http://datamining.typepad.com/"
block|,
literal|"Data Mining: Text Mining, Visualization and Social Media"
block|,
literal|"Commentary on text mining, data mining, social media and data visualization. ... While mining Twitter data for business and marketing intelligence (trend/buzz ..."
block|}
block|,
block|{
literal|"http://www.twocrows.com/"
block|,
literal|"Two Crows Corporation"
block|,
literal|"Dedicated to the development, marketing, sales and support of tools for knowledge discovery to make data mining accessible and easy to use."
block|}
block|,
block|{
literal|"http://www.thearling.com/"
block|,
literal|"Thearling.com"
block|,
literal|"Kurt Thearling's site dedicated to sharing information about data mining, the automated extraction of hidden predictive information from databases, and other analytic technologies."
block|}
block|,
block|{
literal|"http://www.ccsu.edu/datamining/"
block|,
literal|"CCSU - Data Mining"
block|,
literal|"Offers degrees and certificates in data mining. Allows students to explore cutting-edge data mining techniques and applications: market basket analysis, decision trees, neural networks, machine learning, web mining, and data modeling."
block|}
block|,
block|{
literal|"http://www.oracle.com/technology/products/bi/odm"
block|,
literal|"Oracle Data Mining"
block|,
literal|"Oracle Data Mining Product Center ... New Oracle Data Mining Powers New Social CRM Application (more information ... Mining High-Dimensional Data for ..."
block|}
block|,
block|{
literal|"http://databases.about.com/od/datamining/a/datamining.htm"
block|,
literal|"Data Mining: An Introduction"
block|,
literal|"About.com article on how businesses are discovering new trends and patterns of behavior that previously went unnoticed through data mining, automated statistical analysis techniques."
block|}
block|,
block|{
literal|"http://www.dmoz.org/Computers/Software/Databases/Data_Mining/"
block|,
literal|"Open Directory - Computers: Software: Databases: Data Mining"
block|,
literal|"Data Mining and Knowledge Discovery - A peer-reviewed journal publishing ... Data mining creates information assets that an organization can leverage to ..."
block|}
block|,
block|{
literal|"http://www.cs.wisc.edu/dmi/"
block|,
literal|"DMI:Data Mining Institute"
block|,
literal|"Data Mining Institute at UW-Madison ... The Data Mining Institute (DMI) was started on June 1, 1999 at the Computer ... of the Data Mining Group of Microsoft ..."
block|}
block|,
block|{
literal|"http://www.the-data-mine.com/"
block|,
literal|"The Data Mine"
block|,
literal|"Provides information about data mining also known as knowledge discovery in databases (KDD) or simply knowledge discovery. List software, events, organizations, and people working in data mining."
block|}
block|,
block|{
literal|"http://www.statserv.com/datamining.html"
block|,
literal|"St@tServ - About Data Mining"
block|,
literal|"St@tServ Data Mining page ... Data mining in molecular biology, by Alvis Brazma. Graham Williams page. Knowledge Discovery and Data Mining Resources, ..."
block|}
block|,
block|{
literal|"http://ocw.mit.edu/OcwWeb/Sloan-School-of-Management/15-062Data-MiningSpring2003/CourseHome/index.htm"
block|,
literal|"MIT OpenCourseWare | Sloan School of Management | 15.062 Data Mining ..."
block|,
literal|"Introduces students to a class of methods known as data mining that assists managers in recognizing patterns and making intelligent use of massive amounts of ..."
block|}
block|,
block|{
literal|"http://www.pentaho.com/products/data_mining/"
block|,
literal|"Pentaho Commercial Open Source Business Intelligence: Data Mining"
block|,
literal|"For example, data mining can warn you there's a high probability a specific ... Pentaho Data Mining is differentiated by its open, standards-compliant nature, ..."
block|}
block|,
block|{
literal|"http://www.investorhome.com/mining.htm"
block|,
literal|"Investor Home - Data Mining"
block|,
literal|"Data Mining or Data Snooping is the practice of searching for relationships and ... Data mining involves searching through databases for correlations and patterns ..."
block|}
block|,
block|{
literal|"http://www.datamining.com/"
block|,
literal|"Predictive Modeling and Predictive Analytics Solutions | Enterprise ..."
block|,
literal|"Insightful Enterprise Miner - Enterprise data mining for predictive modeling and predictive analytics."
block|}
block|,
block|{
literal|"http://www.sourcewatch.org/index.php?title=Data_mining"
block|,
literal|"Data mining - SourceWatch"
block|,
literal|"These agencies reported 199 data mining projects, of which 68 ... Office, \"DATA MINING. ... powerful technology known as data mining -- and how, in the ..."
block|}
block|,
block|{
literal|"http://www.autonlab.org/tutorials/"
block|,
literal|"Statistical Data Mining Tutorials"
block|,
literal|"Includes a set of tutorials on many aspects of statistical data mining, including the foundations of probability, the foundations of statistical data analysis, and most of the classic machine learning and data mining algorithms."
block|}
block|,
block|{
literal|"http://www.microstrategy.com/data-mining/index.asp"
block|,
literal|"Data Mining"
block|,
literal|"With MicroStrategy, data mining scoring is fully integrated into mainstream ... The integration of data mining models from other applications is accomplished by ..."
block|}
block|,
block|{
literal|"http://www.datamininglab.com/"
block|,
literal|"Elder Research"
block|,
literal|"Provides consulting and short courses in data mining and pattern discovery patterns in data."
block|}
block|,
block|{
literal|"http://www.sqlserverdatamining.com/"
block|,
literal|"SQL Server Data Mining> Home"
block|,
literal|"SQL Server Data Mining Portal ... Data Mining as an Application Platform (Whitepaper) Creating a Web Cross-sell Application with SQL Server 2005 Data Mining (Article) ..."
block|}
block|,
block|{
literal|"http://databases.about.com/cs/datamining/g/dmining.htm"
block|,
literal|"Data Mining"
block|,
literal|"What is data mining? Find out here! ... Book Review: Data Mining and Statistical Analysis Using SQL. What is Data Mining, and What Does it Have to Do with ..."
block|}
block|,
block|{
literal|"http://www.sas.com/technologies/analytics/datamining/index.html"
block|,
literal|"Data Mining Software and Text Mining | SAS"
block|,
literal|"... raw data to smarter ... Data Mining is an iterative process of creating ... The knowledge gleaned from data and text mining can be used to fuel ..."
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
