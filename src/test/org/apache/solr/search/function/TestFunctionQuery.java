begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * Tests some basic functionality of Solr while demonstrating good  * Best Practices for using AbstractSolrTestCase  */
end_comment
begin_class
DECL|class|TestFunctionQuery
specifier|public
class|class
name|TestFunctionQuery
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
name|void
name|createIndex
parameter_list|(
name|String
name|field
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
comment|// lrf.args.put("version","2.0");
for|for
control|(
name|float
name|val
range|:
name|values
control|)
block|{
name|String
name|s
init|=
name|Float
operator|.
name|toString
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|s
argument_list|,
name|field
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"added doc for "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// squeeze out any possible deleted docs
block|}
comment|// replace \0 with the field name and create a parseable string
DECL|method|func
specifier|public
name|String
name|func
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|template
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"_val_:\""
argument_list|)
decl_stmt|;
for|for
control|(
name|char
name|ch
range|:
name|template
operator|.
name|toCharArray
argument_list|()
control|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\0'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|ch
operator|==
literal|'"'
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|singleTest
name|void
name|singleTest
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|funcTemplate
parameter_list|,
name|float
modifier|...
name|results
parameter_list|)
block|{
comment|// lrf.args.put("version","2.0");
name|String
name|parseableQuery
init|=
name|func
argument_list|(
name|field
argument_list|,
name|funcTemplate
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tests
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Construct xpaths like the following:
comment|// "//doc[./float[@name='foo_pf']='10.0' and ./float[@name='score']='10.0']"
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|xpath
init|=
literal|"//doc[./float[@name='"
operator|+
name|field
operator|+
literal|"']='"
operator|+
name|results
index|[
name|i
index|]
operator|+
literal|"' and ./float[@name='score']='"
operator|+
name|results
index|[
name|i
operator|+
literal|1
index|]
operator|+
literal|"']"
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
name|xpath
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|parseableQuery
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
name|tests
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tests
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest
name|void
name|doTest
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|// lrf.args.put("version","2.0");
name|float
index|[]
name|vals
init|=
operator|new
name|float
index|[]
block|{
literal|100
block|,
operator|-
literal|4
block|,
literal|0
block|,
literal|10
block|,
literal|25
block|,
literal|5
block|}
decl_stmt|;
name|createIndex
argument_list|(
name|field
argument_list|,
name|vals
argument_list|)
expr_stmt|;
comment|// test identity (straight field value)
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"\0"
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// test constant score
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"1.414213"
argument_list|,
literal|10
argument_list|,
literal|1.414213f
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"-1.414213"
argument_list|,
literal|10
argument_list|,
operator|-
literal|1.414213f
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sum(\0,1)"
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sum(\0,\0)"
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sum(\0,\0,5)"
argument_list|,
literal|10
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"product(\0,1)"
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"product(\0,-2,-4)"
argument_list|,
literal|10
argument_list|,
literal|80
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"log(\0)"
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sqrt(\0)"
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|25
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"abs(\0)"
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
operator|-
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"pow(\0,\0)"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|3125
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"pow(\0,0.5)"
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|25
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"div(1,\0)"
argument_list|,
operator|-
literal|4
argument_list|,
operator|-
literal|.25f
argument_list|,
literal|10
argument_list|,
literal|.1f
argument_list|,
literal|100
argument_list|,
literal|.01f
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"div(1,1)"
argument_list|,
operator|-
literal|4
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sqrt(abs(\0))"
argument_list|,
operator|-
literal|4
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"sqrt(sum(29,\0))"
argument_list|,
operator|-
literal|4
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"map(\0,0,0,500)"
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
operator|-
literal|4
argument_list|,
operator|-
literal|4
argument_list|,
literal|0
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"map(\0,-4,5,500)"
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
operator|-
literal|4
argument_list|,
literal|500
argument_list|,
literal|0
argument_list|,
literal|500
argument_list|,
literal|5
argument_list|,
literal|500
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|25
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"scale(\0,-1,1)"
argument_list|,
operator|-
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|0.9230769f
argument_list|)
expr_stmt|;
name|singleTest
argument_list|(
name|field
argument_list|,
literal|"scale(\0,-10,1000)"
argument_list|,
operator|-
literal|4
argument_list|,
operator|-
literal|10
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
literal|28.846153f
argument_list|)
expr_stmt|;
block|}
DECL|method|testFunctions
specifier|public
name|void
name|testFunctions
parameter_list|()
block|{
name|doTest
argument_list|(
literal|"foo_pf"
argument_list|)
expr_stmt|;
comment|// a plain float field
name|doTest
argument_list|(
literal|"foo_f"
argument_list|)
expr_stmt|;
comment|// a sortable float field
block|}
block|}
end_class
end_unit
