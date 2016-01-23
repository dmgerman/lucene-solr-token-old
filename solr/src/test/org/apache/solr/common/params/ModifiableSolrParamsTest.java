begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more contributor license  * agreements. See the NOTICE file distributed with this work for additional information regarding  * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License. You may obtain a  * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable  * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"  * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License  * for the specific language governing permissions and limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Unit Test Case for {@link org.apache.solr.common.params.ModifiableSolrParams  * ModifiableSolrParams}  *   * @author kkumar  */
end_comment
begin_class
DECL|class|ModifiableSolrParamsTest
specifier|public
class|class
name|ModifiableSolrParamsTest
extends|extends
name|TestCase
block|{
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|modifiable
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|modifiable
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|testAdd
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|values
index|[
literal|0
index|]
operator|=
literal|null
expr_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|String
index|[]
name|result
init|=
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"params"
argument_list|,
name|values
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddNormal
specifier|public
name|void
name|testAddNormal
parameter_list|()
block|{
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|String
index|[]
name|helloWorld
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|}
decl_stmt|;
name|String
index|[]
name|universe
init|=
operator|new
name|String
index|[]
block|{
literal|"Universe"
block|}
decl_stmt|;
name|String
index|[]
name|helloWorldUniverse
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|,
literal|"Universe"
block|}
decl_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|helloWorld
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checking Hello World: "
argument_list|,
name|helloWorld
argument_list|,
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|universe
argument_list|)
expr_stmt|;
name|String
index|[]
name|result
init|=
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|compareArrays
argument_list|(
literal|"checking Hello World Universe "
argument_list|,
name|helloWorldUniverse
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddNull
specifier|public
name|void
name|testAddNull
parameter_list|()
block|{
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|String
index|[]
name|helloWorld
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|}
decl_stmt|;
name|String
index|[]
name|universe
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|}
decl_stmt|;
name|String
index|[]
name|helloWorldUniverse
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|,
literal|null
block|}
decl_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|helloWorld
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checking Hello World: "
argument_list|,
name|helloWorld
argument_list|,
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|universe
argument_list|)
expr_stmt|;
name|String
index|[]
name|result
init|=
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|compareArrays
argument_list|(
literal|"checking Hello World Universe "
argument_list|,
name|helloWorldUniverse
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testOldZeroLength
specifier|public
name|void
name|testOldZeroLength
parameter_list|()
block|{
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|String
index|[]
name|helloWorld
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
name|String
index|[]
name|universe
init|=
operator|new
name|String
index|[]
block|{
literal|"Universe"
block|}
decl_stmt|;
name|String
index|[]
name|helloWorldUniverse
init|=
operator|new
name|String
index|[]
block|{
literal|"Universe"
block|}
decl_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|helloWorld
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checking Hello World: "
argument_list|,
name|helloWorld
argument_list|,
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|universe
argument_list|)
expr_stmt|;
name|String
index|[]
name|result
init|=
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|compareArrays
argument_list|(
literal|"checking Hello World Universe "
argument_list|,
name|helloWorldUniverse
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddPseudoNull
specifier|public
name|void
name|testAddPseudoNull
parameter_list|()
block|{
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|String
index|[]
name|helloWorld
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|}
decl_stmt|;
name|String
index|[]
name|universe
init|=
operator|new
name|String
index|[]
block|{
literal|"Universe"
block|,
literal|null
block|}
decl_stmt|;
name|String
index|[]
name|helloWorldUniverse
init|=
operator|new
name|String
index|[]
block|{
literal|"Hello"
block|,
literal|"World"
block|,
literal|"Universe"
block|,
literal|null
block|}
decl_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|helloWorld
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checking Hello World: "
argument_list|,
name|helloWorld
argument_list|,
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|modifiable
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|universe
argument_list|)
expr_stmt|;
name|String
index|[]
name|result
init|=
name|modifiable
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|compareArrays
argument_list|(
literal|"checking Hello World Universe "
argument_list|,
name|helloWorldUniverse
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|compareArrays
specifier|private
name|void
name|compareArrays
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
index|[]
name|expected
parameter_list|,
name|String
index|[]
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|prefix
operator|+
literal|"length: "
argument_list|,
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|prefix
operator|+
literal|" index  "
operator|+
name|i
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|modifiable
specifier|private
name|ModifiableSolrParams
name|modifiable
decl_stmt|;
block|}
end_class
end_unit
