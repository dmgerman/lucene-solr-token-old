begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_comment
comment|/**  * Stores information about how to sort documents by terms in an individual  * field.  Fields must be indexed in order to sort by them.  *  *<p>Created: Feb 11, 2004 1:25:29 PM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  * @see Sort  */
end_comment
begin_class
DECL|class|SortField
specifier|public
class|class
name|SortField
implements|implements
name|Serializable
block|{
comment|/** Sort by document score (relevancy).  Sort values are Float and higher    * values are at the front. */
DECL|field|SCORE
specifier|public
specifier|static
specifier|final
name|int
name|SCORE
init|=
literal|0
decl_stmt|;
comment|/** Sort by document number (index order).  Sort values are Integer and lower    * values are at the front. */
DECL|field|DOC
specifier|public
specifier|static
specifier|final
name|int
name|DOC
init|=
literal|1
decl_stmt|;
comment|/** Guess type of sort based on field contents.  A regular expression is used    * to look at the first term indexed for the field and determine if it    * represents an integer number, a floating point number, or just arbitrary    * string characters. */
DECL|field|AUTO
specifier|public
specifier|static
specifier|final
name|int
name|AUTO
init|=
literal|2
decl_stmt|;
comment|/** Sort using term values as Strings.  Sort values are String and lower    * values are at the front. */
DECL|field|STRING
specifier|public
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|3
decl_stmt|;
comment|/** Sort using term values as encoded Integers.  Sort values are Integer and    * lower values are at the front. */
DECL|field|INT
specifier|public
specifier|static
specifier|final
name|int
name|INT
init|=
literal|4
decl_stmt|;
comment|/** Sort using term values as encoded Floats.  Sort values are Float and    * lower values are at the front. */
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|int
name|FLOAT
init|=
literal|5
decl_stmt|;
comment|/** Represents sorting by document score (relevancy). */
DECL|field|FIELD_SCORE
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_SCORE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SCORE
argument_list|)
decl_stmt|;
comment|/** Represents sorting by document number (index order). */
DECL|field|FIELD_DOC
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_DOC
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|DOC
argument_list|)
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|type
specifier|private
name|int
name|type
init|=
name|AUTO
decl_stmt|;
comment|// defaults to determining type dynamically
DECL|field|reverse
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
comment|// defaults to natural order
comment|/** Creates a sort by terms in the given field where the type of term value    * is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be<code>null</code>.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field where    * the type of term value is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be<code>null</code>.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field with the type of term    * values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field with the    * type of term values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|intern
argument_list|()
else|:
name|field
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Returns the name of the field.  Could return<code>null</code>    * if the sort is by SCORE or DOC.    * @return Name of field, possibly<code>null</code>.    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Returns the type of contents in the field.    * @return One of the constants SCORE, DOC, AUTO, STRING, INT or FLOAT.    */
DECL|method|getType
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** Returns whether the sort should be reversed.    * @return  True if natural order should be reversed.    */
DECL|method|getReverse
specifier|public
name|boolean
name|getReverse
parameter_list|()
block|{
return|return
name|reverse
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SCORE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<score>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOC
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<doc>"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
operator|+
name|field
operator|+
literal|"\""
argument_list|)
expr_stmt|;
break|break;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|reverse
condition|?
literal|" DESC"
else|:
literal|" ASC"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
