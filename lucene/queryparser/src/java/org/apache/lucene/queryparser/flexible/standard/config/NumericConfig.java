begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.config
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
name|config
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Objects
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
name|document
operator|.
name|FieldType
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
name|document
operator|.
name|FieldType
operator|.
name|LegacyNumericType
import|;
end_import
begin_comment
comment|/**  * This class holds the configuration used to parse numeric queries and create  * {@link org.apache.lucene.search.LegacyNumericRangeQuery}s.  *   * @see org.apache.lucene.search.LegacyNumericRangeQuery  * @see NumberFormat  */
end_comment
begin_class
DECL|class|NumericConfig
specifier|public
class|class
name|NumericConfig
block|{
DECL|field|precisionStep
specifier|private
name|int
name|precisionStep
decl_stmt|;
DECL|field|format
specifier|private
name|NumberFormat
name|format
decl_stmt|;
DECL|field|type
specifier|private
name|FieldType
operator|.
name|LegacyNumericType
name|type
decl_stmt|;
comment|/**    * Constructs a {@link NumericConfig} object.    *     * @param precisionStep    *          the precision used to index the numeric values    * @param format    *          the {@link NumberFormat} used to parse a {@link String} to    *          {@link Number}    * @param type    *          the numeric type used to index the numeric values    *     * @see NumericConfig#setPrecisionStep(int)    * @see NumericConfig#setNumberFormat(NumberFormat)    * @see #setType(org.apache.lucene.document.FieldType.LegacyNumericType)    */
DECL|method|NumericConfig
specifier|public
name|NumericConfig
parameter_list|(
name|int
name|precisionStep
parameter_list|,
name|NumberFormat
name|format
parameter_list|,
name|LegacyNumericType
name|type
parameter_list|)
block|{
name|setPrecisionStep
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
name|setNumberFormat
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the precision used to index the numeric values    *     * @return the precision used to index the numeric values    *     * @see org.apache.lucene.search.LegacyNumericRangeQuery#getPrecisionStep()    */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|precisionStep
return|;
block|}
comment|/**    * Sets the precision used to index the numeric values    *     * @param precisionStep    *          the precision used to index the numeric values    *     * @see org.apache.lucene.search.LegacyNumericRangeQuery#getPrecisionStep()    */
DECL|method|setPrecisionStep
specifier|public
name|void
name|setPrecisionStep
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
comment|/**    * Returns the {@link NumberFormat} used to parse a {@link String} to    * {@link Number}    *     * @return the {@link NumberFormat} used to parse a {@link String} to    *         {@link Number}    */
DECL|method|getNumberFormat
specifier|public
name|NumberFormat
name|getNumberFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
comment|/**    * Returns the numeric type used to index the numeric values    *     * @return the numeric type used to index the numeric values    */
DECL|method|getType
specifier|public
name|LegacyNumericType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Sets the numeric type used to index the numeric values    *     * @param type the numeric type used to index the numeric values    */
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|LegacyNumericType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type cannot be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Sets the {@link NumberFormat} used to parse a {@link String} to    * {@link Number}    *     * @param format    *          the {@link NumberFormat} used to parse a {@link String} to    *          {@link Number}, cannot be<code>null</code>    */
DECL|method|setNumberFormat
specifier|public
name|void
name|setNumberFormat
parameter_list|(
name|NumberFormat
name|format
parameter_list|)
block|{
if|if
condition|(
name|format
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"format cannot be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|NumericConfig
condition|)
block|{
name|NumericConfig
name|other
init|=
operator|(
name|NumericConfig
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|precisionStep
operator|==
name|other
operator|.
name|precisionStep
operator|&&
name|this
operator|.
name|type
operator|==
name|other
operator|.
name|type
operator|&&
operator|(
name|this
operator|.
name|format
operator|==
name|other
operator|.
name|format
operator|||
operator|(
name|this
operator|.
name|format
operator|.
name|equals
argument_list|(
name|other
operator|.
name|format
argument_list|)
operator|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|precisionStep
argument_list|,
name|type
argument_list|,
name|format
argument_list|)
return|;
block|}
block|}
end_class
end_unit
