begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request.macro
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|macro
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
name|common
operator|.
name|SolrException
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
name|search
operator|.
name|StrParser
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
name|search
operator|.
name|SyntaxError
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
begin_class
DECL|class|MacroExpander
specifier|public
class|class
name|MacroExpander
block|{
DECL|field|MACRO_START
specifier|public
specifier|static
specifier|final
name|String
name|MACRO_START
init|=
literal|"${"
decl_stmt|;
DECL|field|MAX_LEVELS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LEVELS
init|=
literal|25
decl_stmt|;
DECL|field|orig
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|orig
decl_stmt|;
DECL|field|expanded
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|expanded
decl_stmt|;
DECL|field|macroStart
specifier|private
name|String
name|macroStart
init|=
name|MACRO_START
decl_stmt|;
DECL|field|escape
specifier|private
name|char
name|escape
init|=
literal|'\\'
decl_stmt|;
DECL|field|level
specifier|private
name|int
name|level
decl_stmt|;
DECL|method|MacroExpander
specifier|public
name|MacroExpander
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|orig
parameter_list|)
block|{
name|this
operator|.
name|orig
operator|=
name|orig
expr_stmt|;
block|}
DECL|method|expand
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|expand
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
parameter_list|)
block|{
name|MacroExpander
name|mc
init|=
operator|new
name|MacroExpander
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|mc
operator|.
name|expand
argument_list|()
expr_stmt|;
return|return
name|mc
operator|.
name|expanded
return|;
block|}
DECL|method|expand
specifier|public
name|boolean
name|expand
parameter_list|()
block|{
name|this
operator|.
name|expanded
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|orig
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|orig
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|k
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|newK
init|=
name|expand
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|newValues
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|String
name|newV
init|=
name|expand
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|newV
operator|!=
name|v
condition|)
block|{
if|if
condition|(
name|newValues
operator|==
literal|null
condition|)
block|{
name|newValues
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|vv
range|:
name|values
control|)
block|{
if|if
condition|(
name|vv
operator|==
name|v
condition|)
break|break;
name|newValues
operator|.
name|add
argument_list|(
name|vv
argument_list|)
expr_stmt|;
block|}
block|}
name|newValues
operator|.
name|add
argument_list|(
name|newV
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newValues
operator|!=
literal|null
condition|)
block|{
name|values
operator|=
name|newValues
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|newValues
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|k
operator|!=
name|newK
condition|)
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
name|expanded
operator|.
name|put
argument_list|(
name|newK
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|changed
return|;
block|}
DECL|method|expand
specifier|public
name|String
name|expand
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|level
operator|++
expr_stmt|;
try|try
block|{
if|if
condition|(
name|level
operator|>=
name|MAX_LEVELS
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Request template exceeded max nesting of "
operator|+
name|MAX_LEVELS
operator|+
literal|" expanding '"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|_expand
argument_list|(
name|val
argument_list|)
return|;
block|}
finally|finally
block|{
name|level
operator|--
expr_stmt|;
block|}
block|}
DECL|method|_expand
specifier|private
name|String
name|_expand
parameter_list|(
name|String
name|val
parameter_list|)
block|{
comment|// quickest short circuit
name|int
name|idx
init|=
name|val
operator|.
name|indexOf
argument_list|(
name|macroStart
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
return|return
name|val
return|;
name|int
name|start
init|=
literal|0
decl_stmt|;
comment|// start of the unprocessed part of the string
name|int
name|end
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|idx
operator|=
name|val
operator|.
name|indexOf
argument_list|(
name|macroStart
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|int
name|matchedStart
init|=
name|idx
decl_stmt|;
comment|// check if escaped
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|// check if escaped...
comment|// TODO: what if you *want* to actually have a backslash... perhaps that's when we allow changing
comment|// of the escape character?
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|idx
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
name|escape
condition|)
block|{
name|idx
operator|+=
name|macroStart
operator|.
name|length
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
elseif|else
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
return|return
name|val
return|;
name|sb
operator|.
name|append
argument_list|(
name|val
operator|.
name|substring
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// found unescaped "${"
name|idx
operator|+=
name|macroStart
operator|.
name|length
argument_list|()
expr_stmt|;
name|int
name|rbrace
init|=
name|val
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|,
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|rbrace
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no matching close brace...
continue|continue;
block|}
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
name|val
operator|.
name|length
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|matchedStart
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|val
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|matchedStart
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// update "start" to be at the end of ${...}
name|start
operator|=
name|rbrace
operator|+
literal|1
expr_stmt|;
comment|// String inbetween = val.substring(idx, rbrace);
name|StrParser
name|parser
init|=
operator|new
name|StrParser
argument_list|(
name|val
argument_list|,
name|idx
argument_list|,
name|rbrace
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|paramName
init|=
name|parser
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|defVal
init|=
literal|null
decl_stmt|;
name|boolean
name|hasDefault
init|=
name|parser
operator|.
name|opt
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasDefault
condition|)
block|{
name|defVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|parser
operator|.
name|pos
argument_list|,
name|rbrace
argument_list|)
expr_stmt|;
block|}
comment|// in the event that expansions become context dependent... consult original?
name|String
index|[]
name|replacementList
init|=
name|orig
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
comment|// TODO - handle a list somehow...
name|String
name|replacement
init|=
name|replacementList
operator|!=
literal|null
condition|?
name|replacementList
index|[
literal|0
index|]
else|:
name|defVal
decl_stmt|;
if|if
condition|(
name|replacement
operator|!=
literal|null
condition|)
block|{
name|String
name|expandedReplacement
init|=
name|expand
argument_list|(
name|replacement
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|expandedReplacement
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|syntaxError
parameter_list|)
block|{
comment|// append the part we would have skipped
name|sb
operator|.
name|append
argument_list|(
name|val
operator|.
name|substring
argument_list|(
name|matchedStart
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
block|}
block|}
end_class
end_unit
