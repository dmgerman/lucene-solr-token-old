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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakAction
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakLingering
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakZombies
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
name|Iterator
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
name|Locale
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_class
annotation|@
name|ThreadLeakAction
argument_list|(
block|{
name|ThreadLeakAction
operator|.
name|Action
operator|.
name|WARN
block|}
argument_list|)
annotation|@
name|ThreadLeakLingering
argument_list|(
name|linger
operator|=
literal|0
argument_list|)
annotation|@
name|ThreadLeakZombies
argument_list|(
name|ThreadLeakZombies
operator|.
name|Consequence
operator|.
name|CONTINUE
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|ThreadLeakScope
operator|.
name|Scope
operator|.
name|NONE
argument_list|)
DECL|class|TestJdbcDataSourceConvertType
specifier|public
class|class
name|TestJdbcDataSourceConvertType
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|method|testConvertType
specifier|public
name|void
name|testConvertType
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Locale
name|loc
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"Derby is not happy with locale sr-Latn-*"
argument_list|,
name|Objects
operator|.
name|equals
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"sr"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|,
name|loc
operator|.
name|getLanguage
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
literal|"Latn"
argument_list|,
name|loc
operator|.
name|getScript
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// ironically convertType=false causes BigDecimal to String conversion
name|convertTypeTest
argument_list|(
literal|"false"
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// convertType=true uses the "long" conversion (see mapping of some_i to "long")
name|convertTypeTest
argument_list|(
literal|"true"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|convertTypeTest
specifier|private
name|void
name|convertTypeTest
parameter_list|(
name|String
name|convertType
parameter_list|,
name|Class
name|resultClass
parameter_list|)
throws|throws
name|Throwable
block|{
name|JdbcDataSource
name|dataSource
init|=
operator|new
name|JdbcDataSource
argument_list|()
decl_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"driver"
argument_list|,
literal|"org.apache.derby.jdbc.EmbeddedDriver"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"url"
argument_list|,
literal|"jdbc:derby:memory:tempDB;create=true;territory=en_US"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"convertType"
argument_list|,
name|convertType
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|flds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|f
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
literal|"some_i"
argument_list|)
expr_stmt|;
name|f
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
expr_stmt|;
name|flds
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|dataSource
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|flds
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|dataSource
operator|.
name|init
argument_list|(
name|c
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|i
init|=
name|dataSource
operator|.
name|getData
argument_list|(
literal|"select 1 as id, CAST(9999 AS DECIMAL) as \"some_i\" from sysibm.sysdummy1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|map
operator|.
name|get
argument_list|(
literal|"some_i"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|resultClass
argument_list|,
name|val
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
