begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|Constants
specifier|public
class|class
name|Constants
block|{
DECL|field|DEFAULT_RUN_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RUN_COUNT
init|=
literal|5
decl_stmt|;
DECL|field|DEFAULT_SCALE_UP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SCALE_UP
init|=
literal|5
decl_stmt|;
DECL|field|DEFAULT_LOG_STEP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LOG_STEP
init|=
literal|1000
decl_stmt|;
DECL|field|BOOLEANS
specifier|public
specifier|static
name|Boolean
index|[]
name|BOOLEANS
init|=
operator|new
name|Boolean
index|[]
block|{
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|TRUE
block|}
decl_stmt|;
DECL|field|DEFAULT_MAXIMUM_DOCUMENTS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAXIMUM_DOCUMENTS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
block|}
end_class
end_unit
