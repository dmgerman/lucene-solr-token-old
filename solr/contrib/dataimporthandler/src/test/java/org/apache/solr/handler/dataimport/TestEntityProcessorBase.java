begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  *<p>  * Test for EntityProcessorBase  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestEntityProcessorBase
specifier|public
class|class
name|TestEntityProcessorBase
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
DECL|method|multiTransformer
specifier|public
name|void
name|multiTransformer
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entity
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
name|entity
operator|.
name|put
argument_list|(
literal|"transformer"
argument_list|,
name|T1
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|T2
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|T3
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"A"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"B"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|MockDataSource
argument_list|()
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entity
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|src
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"A"
argument_list|,
literal|"NA"
argument_list|)
expr_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"B"
argument_list|,
literal|"NA"
argument_list|)
expr_stmt|;
name|EntityProcessorWrapper
name|sep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|SqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|res
init|=
name|sep
operator|.
name|applyTransformer
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|"T1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|"T2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|"T3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|T1
specifier|static
class|class
name|T1
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|aRow
operator|.
name|put
argument_list|(
literal|"T1"
argument_list|,
literal|"T1 called"
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
DECL|class|T2
specifier|static
class|class
name|T2
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|aRow
operator|.
name|put
argument_list|(
literal|"T2"
argument_list|,
literal|"T2 called"
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
DECL|class|T3
specifier|static
class|class
name|T3
block|{
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|)
block|{
name|aRow
operator|.
name|put
argument_list|(
literal|"T3"
argument_list|,
literal|"T3 called"
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
block|}
end_class
end_unit
