begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|StrUtils
import|;
end_import
begin_class
DECL|class|RequestInfo
specifier|public
class|class
name|RequestInfo
block|{
DECL|field|command
specifier|private
specifier|final
name|String
name|command
decl_stmt|;
DECL|field|debug
specifier|private
specifier|final
name|boolean
name|debug
decl_stmt|;
DECL|field|syncMode
specifier|private
specifier|final
name|boolean
name|syncMode
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|boolean
name|commit
decl_stmt|;
DECL|field|optimize
specifier|private
specifier|final
name|boolean
name|optimize
decl_stmt|;
DECL|field|start
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|rows
specifier|private
specifier|final
name|long
name|rows
decl_stmt|;
DECL|field|clean
specifier|private
specifier|final
name|boolean
name|clean
decl_stmt|;
DECL|field|entitiesToRun
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|entitiesToRun
decl_stmt|;
DECL|field|rawParams
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rawParams
decl_stmt|;
DECL|field|dataConfig
specifier|private
specifier|final
name|String
name|dataConfig
decl_stmt|;
comment|//TODO:  find a different home for these two...
DECL|field|contentStream
specifier|private
specifier|final
name|ContentStream
name|contentStream
decl_stmt|;
DECL|field|debugInfo
specifier|private
specifier|final
name|DebugInfo
name|debugInfo
decl_stmt|;
DECL|method|RequestInfo
specifier|public
name|RequestInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestParams
parameter_list|,
name|ContentStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|contentStream
operator|=
name|stream
expr_stmt|;
if|if
condition|(
name|requestParams
operator|.
name|containsKey
argument_list|(
literal|"command"
argument_list|)
condition|)
block|{
name|command
operator|=
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"command"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|command
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|debugMode
init|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"debug"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|debugMode
condition|)
block|{
name|debug
operator|=
literal|true
expr_stmt|;
name|debugInfo
operator|=
operator|new
name|DebugInfo
argument_list|(
name|requestParams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|debug
operator|=
literal|false
expr_stmt|;
name|debugInfo
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|requestParams
operator|.
name|containsKey
argument_list|(
literal|"clean"
argument_list|)
condition|)
block|{
name|clean
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"clean"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DataImporter
operator|.
name|DELTA_IMPORT_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
operator|||
name|DataImporter
operator|.
name|IMPORT_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|clean
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|clean
operator|=
name|debug
condition|?
literal|false
else|:
literal|true
expr_stmt|;
block|}
name|optimize
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"optimize"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|optimize
condition|)
block|{
name|commit
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|commit
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"commit"
argument_list|)
argument_list|,
operator|(
name|debug
condition|?
literal|false
else|:
literal|true
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestParams
operator|.
name|containsKey
argument_list|(
literal|"rows"
argument_list|)
condition|)
block|{
name|rows
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"rows"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rows
operator|=
name|debug
condition|?
literal|10
else|:
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|requestParams
operator|.
name|containsKey
argument_list|(
literal|"start"
argument_list|)
condition|)
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"start"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
literal|0
expr_stmt|;
block|}
name|syncMode
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"synchronous"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|requestParams
operator|.
name|get
argument_list|(
literal|"entity"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|modifiableEntities
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|modifiableEntities
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|modifiableEntities
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|modifiableEntities1
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|o
argument_list|)
decl_stmt|;
name|modifiableEntities
operator|=
name|modifiableEntities1
expr_stmt|;
block|}
name|entitiesToRun
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modifiableEntities
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entitiesToRun
operator|=
literal|null
expr_stmt|;
block|}
name|String
name|dataConfigParam
init|=
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"dataConfig"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataConfigParam
operator|!=
literal|null
operator|&&
name|dataConfigParam
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Empty data-config param is not valid, change it to null
name|dataConfigParam
operator|=
literal|null
expr_stmt|;
block|}
name|dataConfig
operator|=
name|dataConfigParam
expr_stmt|;
name|this
operator|.
name|rawParams
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|requestParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommand
specifier|public
name|String
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
DECL|method|isDebug
specifier|public
name|boolean
name|isDebug
parameter_list|()
block|{
return|return
name|debug
return|;
block|}
DECL|method|isSyncMode
specifier|public
name|boolean
name|isSyncMode
parameter_list|()
block|{
return|return
name|syncMode
return|;
block|}
DECL|method|isCommit
specifier|public
name|boolean
name|isCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
DECL|method|isOptimize
specifier|public
name|boolean
name|isOptimize
parameter_list|()
block|{
return|return
name|optimize
return|;
block|}
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getRows
specifier|public
name|long
name|getRows
parameter_list|()
block|{
return|return
name|rows
return|;
block|}
DECL|method|isClean
specifier|public
name|boolean
name|isClean
parameter_list|()
block|{
return|return
name|clean
return|;
block|}
comment|/**    * Returns null if we are to run all entities, otherwise just run the entities named in the list.    */
DECL|method|getEntitiesToRun
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getEntitiesToRun
parameter_list|()
block|{
return|return
name|entitiesToRun
return|;
block|}
DECL|method|getDataConfig
specifier|public
name|String
name|getDataConfig
parameter_list|()
block|{
return|return
name|dataConfig
return|;
block|}
DECL|method|getRawParams
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRawParams
parameter_list|()
block|{
return|return
name|rawParams
return|;
block|}
DECL|method|getContentStream
specifier|public
name|ContentStream
name|getContentStream
parameter_list|()
block|{
return|return
name|contentStream
return|;
block|}
DECL|method|getDebugInfo
specifier|public
name|DebugInfo
name|getDebugInfo
parameter_list|()
block|{
return|return
name|debugInfo
return|;
block|}
block|}
end_class
end_unit
