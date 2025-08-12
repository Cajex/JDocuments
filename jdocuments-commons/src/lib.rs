use chrono::Local;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Copy, Clone)]
pub enum CloudUniqueID {
    LinkID(usize),
    DocumentID(usize),
}

#[derive(Serialize, Deserialize)]
pub struct CloudDocumentObject {
    title: Option<String>,
    author: Option<String>,
    vc: VersionControlObject,
    id: CloudUniqueID,
}

#[derive(Serialize, Deserialize)]
pub struct VersionControlObject {
    proceedings: Vec<VersionControlAction>,
}

#[derive(Serialize, Deserialize)]
pub enum VersionControlAction {
    Origin(String, String),
    Insert(String),
    Requested(String, String),
    Changed(String, String),
}

#[derive(Serialize, Deserialize)]
pub struct CloudTagObject {
    hex_color: String,
    name: String,
    note: Option<String>,
    objects: Vec<CloudUniqueID>,
}

#[derive(Serialize, Deserialize)]
pub struct CloudLinkObject {
    declaration: String,
    id: CloudUniqueID,
    note: String,
    inner_links: Vec<CloudUniqueID>,
}

pub trait CloudObject {
    type Type;

    fn list_to_pretty_json(list: &Vec<Self>) -> String
    where
        Self: Sized,
        Self: Serialize,
    {
        serde_json::to_string_pretty(list).unwrap()
    }

    fn list_to_json(list: &Vec<Self>) -> String
    where
        Self: Sized,
        Self: Serialize,
    {
        serde_json::to_string(list).unwrap()
    }

    fn list_from_json<'a>(str: &'a str) -> Vec<Self>
    where
        Self: Sized,
        Self: Deserialize<'a>,
    {
        serde_json::from_slice(str.as_bytes()).unwrap()
    }
}

impl CloudObject for CloudDocumentObject {
    type Type = Self;
}

impl CloudObject for CloudTagObject {
    type Type = Self;
}

impl CloudObject for CloudLinkObject {
    type Type = Self;
}

impl CloudDocumentObject {
    pub fn insert(
        title: Option<String>,
        author: Option<String>,
        origin_date: Option<String>,
        origin_location: Option<String>,
        attached_tags: Vec<String>,
        attached_links: Vec<String>,
        document_stack: &mut Vec<CloudDocumentObject>,
        tag_stack: &mut Vec<CloudTagObject>,
        link_stack: &mut Vec<CloudLinkObject>,
    ) {
        let document = Self {
            title,
            author,
            vc: VersionControlObject {
                proceedings: vec![
                    VersionControlAction::Origin(
                        origin_date.unwrap_or("Not specified".to_string()),
                        origin_location.unwrap_or("Not specified".to_string()),
                    ),
                    VersionControlAction::Insert(Local::now().format("%d.%m.%Y").to_string()),
                ],
            },
            id: CloudUniqueID::DocumentID(document_stack.len() + 1),
        };

        CloudTagObject::attach_tags(attached_tags, tag_stack, document.id);
        CloudLinkObject::link_document(attached_links, link_stack, document.id);
    }
}

impl CloudTagObject {
    pub fn attach_tags(
        attached_tags: Vec<String>,
        tag_stack: &mut Vec<CloudTagObject>,
        id: CloudUniqueID,
    ) {
        for item in attached_tags {
            Self::attach_tag(item, tag_stack, id);
        }
    }

    pub fn attach_tag(p_tag: String, tag_stack: &mut Vec<CloudTagObject>, id: CloudUniqueID) {
        match tag_stack.iter_mut().find(|tag| tag.name.eq(&p_tag)) {
            None => {
                let tag = CloudTagObject {
                    hex_color: CloudTagObject::get_random_tag_color(),
                    name: p_tag,
                    note: None,
                    objects: vec![id],
                };
                tag_stack.push(tag);
            }
            Some(tag) => tag.objects.push(id),
        }
    }
}

impl CloudLinkObject {
    pub fn link_document(
        attached_links: Vec<String>,
        link_stack: &mut Vec<CloudLinkObject>,
        id: CloudUniqueID,
    ) {
        for item in attached_links {
            Self::single_link_document(item, link_stack, id)
        }
    }

    pub fn single_link_document(
        p_link: String,
        link_stack: &mut Vec<CloudLinkObject>,
        id: CloudUniqueID,
    ) {
        match link_stack
            .iter_mut()
            .find(|link| link.declaration.eq(&p_link))
        {
            None => {
                let tag = CloudLinkObject {
                    declaration: p_link,
                    id,
                    note: "".to_string(),
                    inner_links: vec![id],
                };
                link_stack.push(tag)
            }
            Some(link) => link.inner_links.push(id),
        }
    }
}

impl CloudTagObject {
    pub fn get_random_tag_color() -> String {
        format!(
            "{:02X}{:02X}{:02X}",
            rand::random::<u8>(),
            rand::random::<u8>(),
            rand::random::<u8>()
        )
    }
}
