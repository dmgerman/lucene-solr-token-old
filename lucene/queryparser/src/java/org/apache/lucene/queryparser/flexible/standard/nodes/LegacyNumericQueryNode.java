begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldQueryNode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldValuePairQueryNode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNodeImpl
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|parser
operator|.
name|EscapeQuerySyntax
operator|.
name|Type
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|LegacyNumericConfig
import|;
end_import
begin_comment
comment|/**  * This query node represents a field query that holds a numeric value. It is  * similar to {@link FieldQueryNode}, however the {@link #getValue()} returns a  * {@link Number}.  *   * @see LegacyNumericConfig  * @deprecated Index with Points instead and use {@link PointQueryNode} instead.  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|LegacyNumericQueryNode
specifier|public
class|class
name|LegacyNumericQueryNode
extends|extends
name|QueryNodeImpl
implements|implements
name|FieldValuePairQueryNode
argument_list|<
name|Number
argument_list|>
block|{
DECL|field|numberFormat
specifier|private
name|NumberFormat
name|numberFormat
decl_stmt|;
DECL|field|field
specifier|private
name|CharSequence
name|field
decl_stmt|;
DECL|field|value
specifier|private
name|Number
name|value
decl_stmt|;
comment|/**    * Creates a {@link LegacyNumericQueryNode} object using the given field,    * {@link Number} value and {@link NumberFormat} used to convert the value to    * {@link String}.    *     * @param field the field associated with this query node    * @param value the value hold by this node    * @param numberFormat the {@link NumberFormat} used to convert the value to {@link String}    */
DECL|method|LegacyNumericQueryNode
specifier|public
name|LegacyNumericQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|Number
name|value
parameter_list|,
name|NumberFormat
name|numberFormat
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|setNumberFormat
argument_list|(
name|numberFormat
argument_list|)
expr_stmt|;
name|setField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the field associated with this node.    *     * @return the field associated with this node    */
annotation|@
name|Override
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
comment|/**    * Sets the field associated with this node.    *     * @param fieldName the field associated with this node    */
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**    * This method is used to get the value converted to {@link String} and    * escaped using the given {@link EscapeQuerySyntax}.    *     * @param escaper the {@link EscapeQuerySyntax} used to escape the value {@link String}    *     * @return the value converte to {@link String} and escaped    */
DECL|method|getTermEscaped
specifier|protected
name|CharSequence
name|getTermEscaped
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
return|return
name|escaper
operator|.
name|escape
argument_list|(
name|numberFormat
operator|.
name|format
argument_list|(
name|this
operator|.
name|value
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|,
name|Type
operator|.
name|NORMAL
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
if|if
condition|(
name|isDefaultField
argument_list|(
name|this
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
name|getTermEscaped
argument_list|(
name|escapeSyntaxParser
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|":"
operator|+
name|getTermEscaped
argument_list|(
name|escapeSyntaxParser
argument_list|)
return|;
block|}
block|}
comment|/**    * Sets the {@link NumberFormat} used to convert the value to {@link String}.    *     * @param format the {@link NumberFormat} used to convert the value to {@link String}    */
DECL|method|setNumberFormat
specifier|public
name|void
name|setNumberFormat
parameter_list|(
name|NumberFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|numberFormat
operator|=
name|format
expr_stmt|;
block|}
comment|/**    * Returns the {@link NumberFormat} used to convert the value to {@link String}.    *     * @return the {@link NumberFormat} used to convert the value to {@link String}    */
DECL|method|getNumberFormat
specifier|public
name|NumberFormat
name|getNumberFormat
parameter_list|()
block|{
return|return
name|this
operator|.
name|numberFormat
return|;
block|}
comment|/**    * Returns the numeric value as {@link Number}.    *     * @return the numeric value    */
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Number
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Sets the numeric value.    *     * @param value the numeric value    */
annotation|@
name|Override
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|Number
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<numeric field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' number='"
operator|+
name|numberFormat
operator|.
name|format
argument_list|(
name|value
argument_list|)
operator|+
literal|"'/>"
return|;
block|}
block|}
end_class
end_unit
