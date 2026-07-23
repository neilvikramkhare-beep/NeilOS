package com.neilos.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Note entity class representing a single note in the NeilOS system.
 * This class provides a rich model for note management with metadata,
 * categories, tags, and versioning support.
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class Note {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Maximum note content length (10,000 characters) */
    public static final int MAX_CONTENT_LENGTH = 10000;
    
    /** Maximum tag length (20 characters) */
    public static final int MAX_TAG_LENGTH = 20;
    
    /** Maximum category length (30 characters) */
    public static final int MAX_CATEGORY_LENGTH = 30;
    
    /** Maximum title length (100 characters) */
    public static final int MAX_TITLE_LENGTH = 100;
    
    /** Default category for uncategorized notes */
    public static final String DEFAULT_CATEGORY = "Uncategorized";
    
    /** Default priority for notes */
    public static final int DEFAULT_PRIORITY = 0;
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private Long id;
    private String content;
    private String title;
    private String category;
    private String[] tags;
    private int priority;
    private boolean pinned;
    private boolean archived;
    private boolean encrypted;
    private String color;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reminderAt;
    private LocalDateTime archivedAt;
    private int version;
    private String parentNoteId;
    private String[] attachments;
    private String[] collaborators;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor for creating a new note
     */
    public Note() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.category = DEFAULT_CATEGORY;
        this.priority = DEFAULT_PRIORITY;
        this.pinned = false;
        this.archived = false;
        this.encrypted = false;
        this.version = 1;
        this.tags = new String[0];
        this.attachments = new String[0];
        this.collaborators = new String[0];
        this.color = "#FFFFFF";
        this.createdBy = System.getProperty("user.name");
        this.lastModifiedBy = this.createdBy;
    }
    
    /**
     * Constructor with required fields
     * 
     * @param content The note content
     */
    public Note(String content) {
        this();
        setContent(content);
        this.title = generateTitleFromContent(content);
    }
    
    /**
     * Constructor with content and title
     * 
     * @param content The note content
     * @param title The note title
     */
    public Note(String content, String title) {
        this();
        setContent(content);
        setTitle(title);
    }
    
    /**
     * Full constructor for creating a note with all fields
     * 
     * @param id The note ID
     * @param content The note content
     * @param title The note title
     * @param category The note category
     * @param tags The note tags
     * @param priority The note priority
     * @param pinned Whether the note is pinned
     * @param archived Whether the note is archived
     * @param encrypted Whether the note is encrypted
     * @param color The note color
     * @param createdBy The creator username
     * @param lastModifiedBy The last modifier username
     * @param createdAt The creation timestamp
     * @param updatedAt The last update timestamp
     * @param reminderAt The reminder timestamp
     * @param archivedAt The archive timestamp
     * @param version The note version
     * @param parentNoteId The parent note ID for notes
     * @param attachments The attachment paths
     * @param collaborators The collaborator usernames
     */
    public Note(Long id, String content, String title, String category, String[] tags,
                int priority, boolean pinned, boolean archived, boolean encrypted,
                String color, String createdBy, String lastModifiedBy,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime reminderAt,
                LocalDateTime archivedAt, int version, String parentNoteId,
                String[] attachments, String[] collaborators) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.category = category != null ? category : DEFAULT_CATEGORY;
        this.tags = tags != null ? tags : new String[0];
        this.priority = priority;
        this.pinned = pinned;
        this.archived = archived;
        this.encrypted = encrypted;
        this.color = color != null ? color : "#FFFFFF";
        this.createdBy = createdBy != null ? createdBy : System.getProperty("user.name");
        this.lastModifiedBy = lastModifiedBy != null ? lastModifiedBy : this.createdBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.reminderAt = reminderAt;
        this.archivedAt = archivedAt;
        this.version = version;
        this.parentNoteId = parentNoteId;
        this.attachments = attachments != null ? attachments : new String[0];
        this.collaborators = collaborators != null ? collaborators : new String[0];
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        if (content == null) {
            this.content = "";
            return;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                "Note content exceeds maximum length of " + MAX_CONTENT_LENGTH + " characters"
            );
        }
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if (title != null && title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                "Note title exceeds maximum length of " + MAX_TITLE_LENGTH + " characters"
            );
        }
        this.title = title;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        if (category != null && category.length() > MAX_CATEGORY_LENGTH) {
            throw new IllegalArgumentException(
                "Note category exceeds maximum length of " + MAX_CATEGORY_LENGTH + " characters"
            );
        }
        this.category = category != null ? category : DEFAULT_CATEGORY;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String[] getTags() {
        return tags.clone();
    }
    
    public void setTags(String[] tags) {
        if (tags != null) {
            for (String tag : tags) {
                if (tag.length() > MAX_TAG_LENGTH) {
                    throw new IllegalArgumentException(
                        "Tag '" + tag + "' exceeds maximum length of " + MAX_TAG_LENGTH + " characters"
                    );
                }
            }
            this.tags = tags.clone();
        } else {
            this.tags = new String[0];
        }
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        if (tag.length() > MAX_TAG_LENGTH) {
            throw new IllegalArgumentException(
                "Tag '" + tag + "' exceeds maximum length of " + MAX_TAG_LENGTH + " characters"
            );
        }
        // Check if tag already exists
        for (String existing : tags) {
            if (existing.equalsIgnoreCase(tag)) {
                return;
            }
        }
        String[] newTags = new String[tags.length + 1];
        System.arraycopy(tags, 0, newTags, 0, tags.length);
        newTags[tags.length] = tag.trim();
        this.tags = newTags;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void removeTag(String tag) {
        if (tag == null || tags.length == 0) {
            return;
        }
        int index = -1;
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equalsIgnoreCase(tag)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            String[] newTags = new String[tags.length - 1];
            System.arraycopy(tags, 0, newTags, 0, index);
            System.arraycopy(tags, index + 1, newTags, index, tags.length - index - 1);
            this.tags = newTags;
            this.updatedAt = LocalDateTime.now();
            this.version++;
        }
    }
    
    public boolean hasTag(String tag) {
        if (tag == null) {
            return false;
        }
        for (String t : tags) {
            if (t.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isPinned() {
        return pinned;
    }
    
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isArchived() {
        return archived;
    }
    
    public void setArchived(boolean archived) {
        this.archived = archived;
        if (archived) {
            this.archivedAt = LocalDateTime.now();
        } else {
            this.archivedAt = null;
        }
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isEncrypted() {
        return encrypted;
    }
    
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        if (color != null && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid color format. Must be #RRGGBB");
        }
        this.color = color != null ? color : "#FFFFFF";
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy != null ? createdBy : System.getProperty("user.name");
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy != null ? lastModifiedBy : System.getProperty("user.name");
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    public LocalDateTime getReminderAt() {
        return reminderAt;
    }
    
    public void setReminderAt(LocalDateTime reminderAt) {
        this.reminderAt = reminderAt;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getParentNoteId() {
        return parentNoteId;
    }
    
    public void setParentNoteId(String parentNoteId) {
        this.parentNoteId = parentNoteId;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String[] getAttachments() {
        return attachments.clone();
    }
    
    public void setAttachments(String[] attachments) {
        this.attachments = attachments != null ? attachments.clone() : new String[0];
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void addAttachment(String attachmentPath) {
        if (attachmentPath == null || attachmentPath.trim().isEmpty()) {
            return;
        }
        String[] newAttachments = new String[attachments.length + 1];
        System.arraycopy(attachments, 0, newAttachments, 0, attachments.length);
        newAttachments[attachments.length] = attachmentPath.trim();
        this.attachments = newAttachments;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void removeAttachment(String attachmentPath) {
        if (attachmentPath == null || attachments.length == 0) {
            return;
        }
        int index = -1;
        for (int i = 0; i < attachments.length; i++) {
            if (attachments[i].equals(attachmentPath)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            String[] newAttachments = new String[attachments.length - 1];
            System.arraycopy(attachments, 0, newAttachments, 0, index);
            System.arraycopy(attachments, index + 1, newAttachments, index, attachments.length - index - 1);
            this.attachments = newAttachments;
            this.updatedAt = LocalDateTime.now();
            this.version++;
        }
    }
    
    public String[] getCollaborators() {
        return collaborators.clone();
    }
    
    public void setCollaborators(String[] collaborators) {
        this.collaborators = collaborators != null ? collaborators.clone() : new String[0];
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void addCollaborator(String collaborator) {
        if (collaborator == null || collaborator.trim().isEmpty()) {
            return;
        }
        // Check if collaborator already exists
        for (String existing : collaborators) {
            if (existing.equalsIgnoreCase(collaborator)) {
                return;
            }
        }
        String[] newCollaborators = new String[collaborators.length + 1];
        System.arraycopy(collaborators, 0, newCollaborators, 0, collaborators.length);
        newCollaborators[collaborators.length] = collaborator.trim();
        this.collaborators = newCollaborators;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public void removeCollaborator(String collaborator) {
        if (collaborator == null || collaborators.length == 0) {
            return;
        }
        int index = -1;
        for (int i = 0; i < collaborators.length; i++) {
            if (collaborators[i].equalsIgnoreCase(collaborator)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            String[] newCollaborators = new String[collaborators.length - 1];
            System.arraycopy(collaborators, 0, newCollaborators, 0, index);
            System.arraycopy(collaborators, index + 1, newCollaborators, index, collaborators.length - index - 1);
            this.collaborators = newCollaborators;
            this.updatedAt = LocalDateTime.now();
            this.version++;
        }
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Generates a title from the content by taking the first line or first few words
     * 
     * @param content The note content
     * @return A generated title
     */
    private String generateTitleFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Untitled Note";
        }
        
        String[] lines = content.trim().split("\n");
        String firstLine = lines[0].trim();
        
        if (firstLine.isEmpty()) {
            return "Untitled Note";
        }
        
        if (firstLine.length() <= MAX_TITLE_LENGTH) {
            return firstLine;
        }
        
        // Truncate to max title length
        return firstLine.substring(0, MAX_TITLE_LENGTH - 3) + "...";
    }
    
    /**
     * Gets a preview of the note content (first 100 characters)
     * 
     * @return A preview of the note content
     */
    public String getPreview() {
        if (content == null || content.isEmpty()) {
            return "[Empty Note]";
        }
        
        String preview = content.replace('\n', ' ');
        if (preview.length() <= 100) {
            return preview;
        }
        return preview.substring(0, 97) + "...";
    }
    
    /**
     * Gets a preview with custom length
     * 
     * @param length The preview length
     * @return A preview of the note content
     */
    public String getPreview(int length) {
        if (content == null || content.isEmpty()) {
            return "[Empty Note]";
        }
        
        String preview = content.replace('\n', ' ');
        if (preview.length() <= length) {
            return preview;
        }
        return preview.substring(0, Math.max(1, length - 3)) + "...";
    }
    
    /**
     * Gets the word count of the note
     * 
     * @return The word count
     */
    public int getWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
    
    /**
     * Gets the character count of the note
     * 
     * @return The character count
     */
    public int getCharacterCount() {
        return content != null ? content.length() : 0;
    }
    
    /**
     * Checks if the note contains the specified text
     * 
     * @param text The text to search for
     * @return true if the note contains the text
     */
    public boolean containsText(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        if (content == null) {
            return false;
        }
        return content.toLowerCase().contains(text.toLowerCase());
    }
    
    /**
     * Gets the time since last update as a human-readable string
     * 
     * @return The time since last update
     */
    public String getTimeSinceUpdate() {
        return formatTimeDifference(updatedAt);
    }
    
    /**
     * Gets the time since creation as a human-readable string
     * 
     * @return The time since creation
     */
    public String getTimeSinceCreation() {
        return formatTimeDifference(createdAt);
    }
    
    /**
     * Formats the time difference between the given time and now
     * 
     * @param time The time to compare
     * @return A human-readable time difference string
     */
    private String formatTimeDifference(LocalDateTime time) {
        if (time == null) {
            return "Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(time, now);
        
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return "Just now";
        }
        
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        }
        
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        }
        
        long days = hours / 24;
        if (days < 30) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
        
        long months = days / 30;
        if (months < 12) {
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        }
        
        long years = months / 12;
        return years + " year" + (years > 1 ? "s" : "") + " ago";
    }
    
    /**
     * Creates a copy of this note
     * 
     * @return A copy of the note
     */
    public Note copy() {
        Note copy = new Note();
        copy.content = this.content;
        copy.title = this.title != null ? this.title + " (Copy)" : "Untitled Note (Copy)";
        copy.category = this.category;
        copy.tags = this.tags.clone();
        copy.priority = this.priority;
        copy.pinned = false;
        copy.archived = false;
        copy.encrypted = this.encrypted;
        copy.color = this.color;
        copy.createdBy = this.createdBy;
        copy.lastModifiedBy = this.lastModifiedBy;
        copy.createdAt = LocalDateTime.now();
        copy.updatedAt = LocalDateTime.now();
        copy.reminderAt = null;
        copy.version = 1;
        copy.parentNoteId = null;
        copy.attachments = new String[0];
        copy.collaborators = this.collaborators.clone();
        return copy;
    }
    
    // ============================================================
    // OVERRIDE METHODS
    // ============================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Note{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title != null ? title : "Untitled").append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", priority=").append(priority);
        sb.append(", pinned=").append(pinned);
        sb.append(", archived=").append(archived);
        sb.append(", encrypted=").append(encrypted);
        sb.append(", tags=").append(tags.length > 0 ? String.join(", ", tags) : "[]");
        sb.append(", createdBy='").append(createdBy).append('\'');
        sb.append(", createdAt=").append(createdAt != null ? createdAt.format(formatter) : "null");
        sb.append(", updatedAt=").append(updatedAt != null ? updatedAt.format(formatter) : "null");
        sb.append(", version=").append(version);
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Returns a formatted string representation with full details
     * 
     * @return A detailed string representation
     */
    public String toDetailedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(60)).append("\n");
        sb.append("📝 NOTE DETAILS\n");
        sb.append("═".repeat(60)).append("\n");
        sb.append("ID:          ").append(id != null ? id : "Not Saved").append("\n");
        sb.append("Title:       ").append(title != null ? title : "Untitled").append("\n");
        sb.append("Category:    ").append(category).append("\n");
        sb.append("Priority:    ").append(priority).append("\n");
        sb.append("Pinned:      ").append(pinned ? "✅ Yes" : "❌ No").append("\n");
        sb.append("Archived:    ").append(archived ? "✅ Yes" : "❌ No").append("\n");
        sb.append("Encrypted:   ").append(encrypted ? "🔒 Yes" : "🔓 No").append("\n");
        sb.append("Color:       ").append(color).append("\n");
        sb.append("Tags:        ").append(tags.length > 0 ? String.join(", ", tags) : "None").append("\n");
        sb.append("Attachments: ").append(attachments.length > 0 ? String.join(", ", attachments) : "None").append("\n");
        sb.append("Collaborators: ").append(collaborators.length > 0 ? String.join(", ", collaborators) : "None").append("\n");
        sb.append("Created By:  ").append(createdBy).append("\n");
        sb.append("Created At:  ").append(createdAt != null ? createdAt.format(formatter) : "Unknown").append("\n");
        sb.append("Updated At:  ").append(updatedAt != null ? updatedAt.format(formatter) : "Unknown").append("\n");
        sb.append("Reminder At: ").append(reminderAt != null ? reminderAt.format(formatter) : "No reminder").append("\n");
        sb.append("Archived At: ").append(archivedAt != null ? archivedAt.format(formatter) : "N/A").append("\n");
        sb.append("Version:     ").append(version).append("\n");
        sb.append("Parent ID:   ").append(parentNoteId != null ? parentNoteId : "None").append("\n");
        sb.append("\n📄 Content:\n");
        sb.append("─".repeat(60)).append("\n");
        sb.append(content != null ? content : "[Empty Note]").append("\n");
        sb.append("─".repeat(60)).append("\n");
        sb.append("Word Count:  ").append(getWordCount()).append("\n");
        sb.append("Characters:  ").append(getCharacterCount()).append("\n");
        sb.append("═".repeat(60)).append("\n");
        
        return sb.toString();
    }
    
    // ============================================================
    // BUILDER PATTERN
    // ============================================================
    
    /**
     * Builder class for creating Note instances with a fluent API
     */
    public static class Builder {
        private Note note;
        
        public Builder() {
            this.note = new Note();
        }
        
        public Builder id(Long id) {
            note.id = id;
            return this;
        }
        
        public Builder content(String content) {
            note.setContent(content);
            return this;
        }
        
        public Builder title(String title) {
            note.setTitle(title);
            return this;
        }
        
        public Builder category(String category) {
            note.setCategory(category);
            return this;
        }
        
        public Builder tags(String... tags) {
            note.setTags(tags);
            return this;
        }
        
        public Builder addTag(String tag) {
            note.addTag(tag);
            return this;
        }
        
        public Builder priority(int priority) {
            note.setPriority(priority);
            return this;
        }
        
        public Builder pinned(boolean pinned) {
            note.setPinned(pinned);
            return this;
        }
        
        public Builder archived(boolean archived) {
            note.setArchived(archived);
            return this;
        }
        
        public Builder encrypted(boolean encrypted) {
            note.setEncrypted(encrypted);
            return this;
        }
        
        public Builder color(String color) {
            note.setColor(color);
            return this;
        }
        
        public Builder createdBy(String createdBy) {
            note.setCreatedBy(createdBy);
            return this;
        }
        
        public Builder lastModifiedBy(String lastModifiedBy) {
            note.setLastModifiedBy(lastModifiedBy);
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            note.setCreatedAt(createdAt);
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            note.setUpdatedAt(updatedAt);
            return this;
        }
        
        public Builder reminderAt(LocalDateTime reminderAt) {
            note.setReminderAt(reminderAt);
            return this;
        }
        
        public Builder version(int version) {
            note.setVersion(version);
            return this;
        }
        
        public Builder parentNoteId(String parentNoteId) {
            note.setParentNoteId(parentNoteId);
            return this;
        }
        
        public Builder attachments(String... attachments) {
            note.setAttachments(attachments);
            return this;
        }
        
        public Builder addAttachment(String attachmentPath) {
            note.addAttachment(attachmentPath);
            return this;
        }
        
        public Builder collaborators(String... collaborators) {
            note.setCollaborators(collaborators);
            return this;
        }
        
        public Builder addCollaborator(String collaborator) {
            note.addCollaborator(collaborator);
            return this;
        }
        
        public Note build() {
            // Ensure title is set
            if (note.title == null || note.title.trim().isEmpty()) {
                note.title = note.generateTitleFromContent(note.content);
            }
            return note;
        }
    }
    
    // ============================================================
    // FACTORY METHODS
    // ============================================================
    
    /**
     * Creates a new note with the given content
     * 
     * @param content The note content
     * @return A new Note instance
     */
    public static Note create(String content) {
        return new Note(content);
    }
    
    /**
     * Creates a new note with the given content and title
     * 
     * @param content The note content
     * @param title The note title
     * @return A new Note instance
     */
    public static Note create(String content, String title) {
        return new Note(content, title);
    }
    
    /**
     * Creates a new builder instance
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}