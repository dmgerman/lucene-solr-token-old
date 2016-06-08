begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_interface
DECL|interface|CollectionParams
specifier|public
interface|interface
name|CollectionParams
block|{
comment|/** What action **/
DECL|field|ACTION
specifier|public
specifier|final
specifier|static
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|enum|CollectionAction
specifier|public
enum|enum
name|CollectionAction
block|{
DECL|enum constant|CREATE
DECL|enum constant|DELETE
DECL|enum constant|RELOAD
DECL|enum constant|SYNCSHARD
DECL|enum constant|CREATEALIAS
DECL|enum constant|DELETEALIAS
DECL|enum constant|SPLITSHARD
DECL|enum constant|DELETESHARD
DECL|enum constant|CREATESHARD
DECL|enum constant|DELETEREPLICA
DECL|enum constant|MIGRATE
DECL|enum constant|ADDROLE
DECL|enum constant|REMOVEROLE
name|CREATE
block|,
name|DELETE
block|,
name|RELOAD
block|,
name|SYNCSHARD
block|,
name|CREATEALIAS
block|,
name|DELETEALIAS
block|,
name|SPLITSHARD
block|,
name|DELETESHARD
block|,
name|CREATESHARD
block|,
name|DELETEREPLICA
block|,
name|MIGRATE
block|,
name|ADDROLE
block|,
name|REMOVEROLE
block|;
DECL|method|get
specifier|public
specifier|static
name|CollectionAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|CollectionAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_interface
end_unit
