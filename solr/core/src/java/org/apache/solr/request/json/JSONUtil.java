begin_unit
begin_package
DECL|package|org.apache.solr.request.json
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|json
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_class
DECL|class|JSONUtil
specifier|public
class|class
name|JSONUtil
block|{
DECL|method|advanceToMapKey
specifier|public
specifier|static
name|boolean
name|advanceToMapKey
parameter_list|(
name|JSONParser
name|parser
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|deepSearch
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
name|parser
operator|.
name|wasKey
argument_list|()
condition|)
block|{
name|String
name|val
init|=
name|parser
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
break|break;
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
return|return
literal|false
return|;
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
if|if
condition|(
name|deepSearch
condition|)
block|{
name|boolean
name|found
init|=
name|advanceToMapKey
argument_list|(
name|parser
argument_list|,
name|key
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|advanceToMapKey
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
name|skipArray
argument_list|(
name|parser
argument_list|,
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|skipArray
specifier|public
specifier|static
name|void
name|skipArray
parameter_list|(
name|JSONParser
name|parser
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|deepSearch
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
name|advanceToMapKey
argument_list|(
name|parser
argument_list|,
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
name|skipArray
argument_list|(
name|parser
argument_list|,
name|key
argument_list|,
name|deepSearch
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSONParser
operator|.
name|ARRAY_END
case|:
return|return;
block|}
block|}
block|}
DECL|method|expect
specifier|public
specifier|static
name|void
name|expect
parameter_list|(
name|JSONParser
name|parser
parameter_list|,
name|int
name|parserEventType
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|event
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|!=
name|parserEventType
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"JSON Parser: expected "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|parserEventType
argument_list|)
operator|+
literal|" but got "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|event
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
