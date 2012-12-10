begin_unit
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *<p> Test for TestLineEntityProcessor</p>  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestLineEntityProcessor
specifier|public
class|class
name|TestLineEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
comment|/************************************************************************/
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* we want to create the equiv of :-      *<entity name="list_all_files"       *           processor="LineEntityProcessor"      *           fileName="dummy.lis"      *           />      */
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|LineEntityProcessor
operator|.
name|URL
argument_list|,
literal|"dummy.lis"
argument_list|,
name|LineEntityProcessor
operator|.
name|ACCEPT_LINE_REGEX
argument_list|,
literal|null
argument_list|,
name|LineEntityProcessor
operator|.
name|SKIP_LINE_REGEX
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
comment|//parentEntity
operator|new
name|VariableResolver
argument_list|()
argument_list|,
comment|//resolver
name|getDataSource
argument_list|(
name|filecontents
argument_list|)
argument_list|,
comment|//parentDataSource
name|Context
operator|.
name|FULL_DUMP
argument_list|,
comment|//currProcess
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|//entityFields
name|attrs
comment|//entityAttrs
argument_list|)
decl_stmt|;
name|LineEntityProcessor
name|ep
init|=
operator|new
name|LineEntityProcessor
argument_list|()
decl_stmt|;
name|ep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|/// call the entity processor to the list of lines
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|ep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
literal|"rawLine"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"     rawLine='"
operator|+
name|f
operator|.
name|get
argument_list|(
literal|"rawLine"
argument_list|)
operator|+
literal|"'\n"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/************************************************************************/
DECL|method|testOnly_xml_files
specifier|public
name|void
name|testOnly_xml_files
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* we want to create the equiv of :-      *<entity name="list_all_files"       *           processor="LineEntityProcessor"      *           fileName="dummy.lis"      *           acceptLineRegex="xml"      *           />      */
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|LineEntityProcessor
operator|.
name|URL
argument_list|,
literal|"dummy.lis"
argument_list|,
name|LineEntityProcessor
operator|.
name|ACCEPT_LINE_REGEX
argument_list|,
literal|"xml"
argument_list|,
name|LineEntityProcessor
operator|.
name|SKIP_LINE_REGEX
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
comment|//parentEntity
operator|new
name|VariableResolver
argument_list|()
argument_list|,
comment|//resolver
name|getDataSource
argument_list|(
name|filecontents
argument_list|)
argument_list|,
comment|//parentDataSource
name|Context
operator|.
name|FULL_DUMP
argument_list|,
comment|//currProcess
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|//entityFields
name|attrs
comment|//entityAttrs
argument_list|)
decl_stmt|;
name|LineEntityProcessor
name|ep
init|=
operator|new
name|LineEntityProcessor
argument_list|()
decl_stmt|;
name|ep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|/// call the entity processor to the list of lines
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|ep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
literal|"rawLine"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/************************************************************************/
DECL|method|testOnly_xml_files_no_xsd
specifier|public
name|void
name|testOnly_xml_files_no_xsd
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* we want to create the equiv of :-      *<entity name="list_all_files"       *           processor="LineEntityProcessor"      *           fileName="dummy.lis"      *           acceptLineRegex="\\.xml"      *           omitLineRegex="\\.xsd"      *           />      */
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|LineEntityProcessor
operator|.
name|URL
argument_list|,
literal|"dummy.lis"
argument_list|,
name|LineEntityProcessor
operator|.
name|ACCEPT_LINE_REGEX
argument_list|,
literal|"\\.xml"
argument_list|,
name|LineEntityProcessor
operator|.
name|SKIP_LINE_REGEX
argument_list|,
literal|"\\.xsd"
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
comment|//parentEntity
operator|new
name|VariableResolver
argument_list|()
argument_list|,
comment|//resolver
name|getDataSource
argument_list|(
name|filecontents
argument_list|)
argument_list|,
comment|//parentDataSource
name|Context
operator|.
name|FULL_DUMP
argument_list|,
comment|//currProcess
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|//entityFields
name|attrs
comment|//entityAttrs
argument_list|)
decl_stmt|;
name|LineEntityProcessor
name|ep
init|=
operator|new
name|LineEntityProcessor
argument_list|()
decl_stmt|;
name|ep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|/// call the entity processor to walk the directory
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|ep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
literal|"rawLine"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/************************************************************************/
DECL|method|testNo_xsd_files
specifier|public
name|void
name|testNo_xsd_files
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* we want to create the equiv of :-      *<entity name="list_all_files"       *           processor="LineEntityProcessor"      *           fileName="dummy.lis"      *           omitLineRegex="\\.xsd"      *           />      */
name|Map
name|attrs
init|=
name|createMap
argument_list|(
name|LineEntityProcessor
operator|.
name|URL
argument_list|,
literal|"dummy.lis"
argument_list|,
name|LineEntityProcessor
operator|.
name|SKIP_LINE_REGEX
argument_list|,
literal|"\\.xsd"
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
comment|//parentEntity
operator|new
name|VariableResolver
argument_list|()
argument_list|,
comment|//resolver
name|getDataSource
argument_list|(
name|filecontents
argument_list|)
argument_list|,
comment|//parentDataSource
name|Context
operator|.
name|FULL_DUMP
argument_list|,
comment|//currProcess
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|//entityFields
name|attrs
comment|//entityAttrs
argument_list|)
decl_stmt|;
name|LineEntityProcessor
name|ep
init|=
operator|new
name|LineEntityProcessor
argument_list|()
decl_stmt|;
name|ep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|/// call the entity processor to walk the directory
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|ep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
literal|"rawLine"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * ********************************************************************    */
DECL|method|createField
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createField
parameter_list|(
name|String
name|col
parameter_list|,
comment|// DIH column name
name|String
name|type
parameter_list|,
comment|// field type from schema.xml
name|String
name|srcCol
parameter_list|,
comment|// DIH transformer attribute 'sourceColName'
name|String
name|re
parameter_list|,
comment|// DIH regex attribute 'regex'
name|String
name|rw
parameter_list|,
comment|// DIH regex attribute 'replaceWith'
name|String
name|gn
comment|// DIH regex attribute 'groupNames'
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|vals
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"sourceColName"
argument_list|,
name|srcCol
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"regex"
argument_list|,
name|re
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"replaceWith"
argument_list|,
name|rw
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"groupNames"
argument_list|,
name|gn
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
DECL|method|getDataSource
specifier|private
name|DataSource
argument_list|<
name|Reader
argument_list|>
name|getDataSource
parameter_list|(
specifier|final
name|String
name|xml
parameter_list|)
block|{
return|return
operator|new
name|DataSource
argument_list|<
name|Reader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|Reader
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|field|filecontents
specifier|private
specifier|static
specifier|final
name|String
name|filecontents
init|=
literal|"\n"
operator|+
literal|"# this is what the output from 'find . -ls; looks like, athough the format\n"
operator|+
literal|"# of the time stamp varies depending on the age of the file and your LANG \n"
operator|+
literal|"# env setting\n"
operator|+
literal|"412577   0 drwxr-xr-x  6 user group    204 1 Apr 10:53 /Volumes/spare/ts\n"
operator|+
literal|"412582   0 drwxr-xr-x 13 user group    442 1 Apr 10:18 /Volumes/spare/ts/config\n"
operator|+
literal|"412583  24 -rwxr-xr-x  1 user group   8318 1 Apr 11:10 /Volumes/spare/ts/config/dc.xsd\n"
operator|+
literal|"412584  32 -rwxr-xr-x  1 user group  12847 1 Apr 11:10 /Volumes/spare/ts/config/dcterms.xsd\n"
operator|+
literal|"412585   8 -rwxr-xr-x  1 user group   3156 1 Apr 11:10 /Volumes/spare/ts/config/s-deliver.css\n"
operator|+
literal|"412586 192 -rwxr-xr-x  1 user group  97764 1 Apr 11:10 /Volumes/spare/ts/config/s-deliver.xsl\n"
operator|+
literal|"412587 224 -rwxr-xr-x  1 user group 112700 1 Apr 11:10 /Volumes/spare/ts/config/sml-delivery-2.1.xsd\n"
operator|+
literal|"412588 208 -rwxr-xr-x  1 user group 103419 1 Apr 11:10 /Volumes/spare/ts/config/sml-delivery-norm-2.0.dtd\n"
operator|+
literal|"412589 248 -rwxr-xr-x  1 user group 125296 1 Apr 11:10 /Volumes/spare/ts/config/sml-delivery-norm-2.1.dtd\n"
operator|+
literal|"412590  72 -rwxr-xr-x  1 user group  36256 1 Apr 11:10 /Volumes/spare/ts/config/jm.xsd\n"
operator|+
literal|"412591   8 -rwxr-xr-x  1 user group    990 1 Apr 11:10 /Volumes/spare/ts/config/video.gif\n"
operator|+
literal|"412592   8 -rwxr-xr-x  1 user group   1498 1 Apr 11:10 /Volumes/spare/ts/config/xlink.xsd\n"
operator|+
literal|"412593   8 -rwxr-xr-x  1 user group   1155 1 Apr 11:10 /Volumes/spare/ts/config/xml.xsd\n"
operator|+
literal|"412594   0 drwxr-xr-x  4 user group    136 1 Apr 10:18 /Volumes/spare/ts/acm19\n"
operator|+
literal|"412621   0 drwxr-xr-x 57 user group   1938 1 Apr 10:18 /Volumes/spare/ts/acm19/data\n"
operator|+
literal|"412622  24 -rwxr-xr-x  1 user group   8894 1 Apr 11:09 /Volumes/spare/ts/acm19/data/00000510.xml\n"
operator|+
literal|"412623  32 -rwxr-xr-x  1 user group  14124 1 Apr 11:09 /Volumes/spare/ts/acm19/data/00000603.xml\n"
operator|+
literal|"412624  24 -rwxr-xr-x  1 user group  11976 1 Apr 11:09 /Volumes/spare/ts/acm19/data/00001292.xml\n"
operator|+
literal|"# tacked on an extra line to cause a file to be deleted.\n"
operator|+
literal|"DELETE /Volumes/spare/ts/acm19/data/00001292old.xml\n"
operator|+
literal|""
decl_stmt|;
block|}
end_class
end_unit
