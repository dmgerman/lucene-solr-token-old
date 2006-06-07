begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package
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
name|io
operator|.
name|StringWriter
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
name|Document
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
name|Field
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|common
operator|.
name|xml
operator|.
name|XmlWriter
import|;
end_import
begin_comment
comment|/**   * This immutable class wrapps Entries for an internal Storage representation of   * an entry. This class also acts as a Documentfactory for lucene documents to   * be stored inside the index.   *    * @author Simon Willnauer   *    */
end_comment
begin_class
DECL|class|StorageEntryWrapper
specifier|public
class|class
name|StorageEntryWrapper
implements|implements
name|Comparable
argument_list|<
name|StorageEntryWrapper
argument_list|>
block|{
DECL|field|INTERNAL_ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|INTERNAL_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
comment|/**       * lucene field name Entry id       */
DECL|field|FIELD_ENTRY_ID
specifier|public
specifier|final
specifier|static
name|String
name|FIELD_ENTRY_ID
init|=
literal|"entryId"
decl_stmt|;
comment|/**       * lucene field name feed id       */
DECL|field|FIELD_FEED_ID
specifier|public
specifier|final
specifier|static
name|String
name|FIELD_FEED_ID
init|=
literal|"feedId"
decl_stmt|;
comment|/**       * lucene field name entry content       */
DECL|field|FIELD_CONTENT
specifier|public
specifier|final
specifier|static
name|String
name|FIELD_CONTENT
init|=
literal|"content"
decl_stmt|;
comment|/**       * lucene field name creating timestamp       */
DECL|field|FIELD_TIMESTAMP
specifier|public
specifier|final
specifier|static
name|String
name|FIELD_TIMESTAMP
init|=
literal|"timestamp"
decl_stmt|;
DECL|field|entryId
specifier|private
specifier|final
name|String
name|entryId
decl_stmt|;
DECL|field|feedId
specifier|private
specifier|final
name|String
name|feedId
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|String
name|content
decl_stmt|;
DECL|field|entry
specifier|private
specifier|final
specifier|transient
name|BaseEntry
name|entry
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|Long
name|timestamp
decl_stmt|;
DECL|field|document
specifier|private
specifier|transient
name|Document
name|document
decl_stmt|;
DECL|field|operation
specifier|private
name|StorageOperation
name|operation
decl_stmt|;
DECL|field|profile
specifier|private
specifier|final
name|ExtensionProfile
name|profile
decl_stmt|;
comment|/**       * Creates a new StorageEntryWrapper.       *        * @param entry -       *            the entry to wrap       * @param feedId -       *            the feed id       * @param operation -       *            the StorageOperation       * @param profile -       *            the ExtensionProfil for the given entry       * @throws IOException -       *             if the entry content can not be generated       */
DECL|method|StorageEntryWrapper
specifier|protected
name|StorageEntryWrapper
parameter_list|(
specifier|final
name|BaseEntry
name|entry
parameter_list|,
specifier|final
name|String
name|feedId
parameter_list|,
name|StorageOperation
name|operation
parameter_list|,
specifier|final
name|ExtensionProfile
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|entryId
operator|=
name|entry
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedId
operator|=
name|feedId
expr_stmt|;
name|this
operator|.
name|profile
operator|=
name|profile
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|buildContent
argument_list|()
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
operator|new
name|Long
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|buildContent
specifier|private
name|String
name|buildContent
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|XmlWriter
name|xmlWriter
init|=
operator|new
name|XmlWriter
argument_list|(
name|writer
argument_list|,
name|INTERNAL_ENCODING
argument_list|)
decl_stmt|;
name|this
operator|.
name|entry
operator|.
name|generateAtom
argument_list|(
name|xmlWriter
argument_list|,
name|this
operator|.
name|profile
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**       * @return - the lucene document representing the entry       */
DECL|method|getLuceneDocument
specifier|public
name|Document
name|getLuceneDocument
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|document
operator|!=
literal|null
condition|)
return|return
name|this
operator|.
name|document
return|;
name|this
operator|.
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|this
operator|.
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"entryId"
argument_list|,
name|this
operator|.
name|entryId
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"feedId"
argument_list|,
name|this
operator|.
name|feedId
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|this
operator|.
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"timestamp"
argument_list|,
name|this
operator|.
name|timestamp
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|document
return|;
block|}
comment|/**       * @return - the wrapped entry       */
DECL|method|getEntry
specifier|public
name|BaseEntry
name|getEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|entry
return|;
block|}
comment|/**       * @return - the entry id       */
DECL|method|getEntryId
specifier|public
name|String
name|getEntryId
parameter_list|()
block|{
return|return
name|this
operator|.
name|entryId
return|;
block|}
comment|/**       * @return - the feed id       */
DECL|method|getFeedId
specifier|public
name|String
name|getFeedId
parameter_list|()
block|{
return|return
name|this
operator|.
name|feedId
return|;
block|}
comment|/**       * Storage operations       *        * @author Simon Willnauer       *        */
DECL|enum|StorageOperation
specifier|public
specifier|static
enum|enum
name|StorageOperation
block|{
comment|/**           * delete           */
DECL|enum constant|DELETE
name|DELETE
block|,
comment|/**           * update           */
DECL|enum constant|UPDATE
name|UPDATE
block|,
comment|/**           * insert           */
DECL|enum constant|INSERT
name|INSERT
block|}
comment|/**       * @return the specified storage operation       */
DECL|method|getOperation
specifier|public
name|StorageOperation
name|getOperation
parameter_list|()
block|{
return|return
name|this
operator|.
name|operation
return|;
block|}
comment|/**       * @see java.lang.Comparable#compareTo(T)       */
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|StorageEntryWrapper
name|arg0
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|timestamp
operator|==
name|this
operator|.
name|timestamp
condition|?
literal|0
else|:
operator|(
name|arg0
operator|.
name|timestamp
operator|>
name|this
operator|.
name|timestamp
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
return|;
block|}
block|}
end_class
end_unit
